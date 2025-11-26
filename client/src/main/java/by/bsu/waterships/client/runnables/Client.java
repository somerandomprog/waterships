package by.bsu.waterships.client.runnables;


import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.messages.*;
import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.MessageResult;
import by.bsu.waterships.shared.utils.ThrowableUtils;
import javafx.application.Platform;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Client extends Thread {
    public interface ClientListener {
        void onConnect();

        void onDisconnect();

        void onError(Exception e);
    }

    public interface ClientCommandListener {
        void onMessage(Message message) throws Exception;
    }

    private static Client instance;

    private final ConcurrentHashMap<String, Message> sourceMessages = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<MessageResult>> pendingMessages = new ConcurrentHashMap<>();

    private ClientListener listener;
    private ConcurrentLinkedQueue<ClientCommandListener> commandListeners = new ConcurrentLinkedQueue<>();
    private String host;
    private boolean connected;

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private Client() {
    }

    private Client(String host) {
        this.host = host;
        addCommandListener(message -> {
            if (message.getCode() == MessageCode.PING) {
                sendMessageWithoutResponse(message.respond(new PingMessageResult()));
            } else if (message.getCode() == MessageCode.HANDSHAKE) {
                GameState.getInstance().index = ((HandshakeMessage) message).index;
                sendMessageWithoutResponse(new HandshakeMessageResult());
                connected = true;
                if (listener != null) Platform.runLater(() -> listener.onConnect());
            }
        });
    }

    public void setListener(ClientListener listener) {
        this.listener = listener;
    }

    public void addCommandListener(ClientCommandListener listener) {
        this.commandListeners.add(listener);
    }

    public void removeCommandListener(ClientCommandListener listener) {
        this.commandListeners.remove(listener);
    }

    public static Client getInstance() {
        return instance;
    }

    public static Client getInstance(String host) {
        if (instance != null && !instance.host.equals(host)) instance.disconnect();
        instance = new Client(host);
        return instance;
    }

    @Override
    public void run() {
        attempt(() -> {
            socket = new Socket(host, Constants.PORT);
            socket.setSoTimeout(Constants.KEEPALIVE_DELAY);
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("initialized socket to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Message message = ThrowableUtils.nullIfThrows(() -> (Message) ois.readObject());
                    if (message == null) continue;
                    if (message instanceof MessageResult && pendingMessages.containsKey(message.getCorrelationId())) {
                        System.out.printf("[%s -> %s] %s\n", sourceMessages.get(message.getCorrelationId()).getCode(), message.getCode(), message);
                        pendingMessages.get(message.getCorrelationId()).complete((MessageResult) message);
                        pendingMessages.remove(message.getCorrelationId());
                        sourceMessages.remove(message.getCorrelationId());
                    } else {
                        System.out.printf("[%s] %s\n", message.getCode(), message);
                        for (ClientCommandListener commandListener : commandListeners)
                            commandListener.onMessage(message);
                    }
                }
            } catch (InterruptedException e) {
                interrupt();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            } finally {
                disconnect();
            }
        });
    }

    public void disconnect() {
        if (!connected) return;
        attempt(() -> {
            sendMessageWithoutResponse(new DisconnectMessage());
            connected = false;
            cleanup();
            if (listener != null) listener.onDisconnect();
            interrupt();
        });
    }

    private void cleanup() {
        try {
            if (oos != null) oos.close();
            if (ois != null) ois.close();
            if (socket != null && !socket.isClosed()) socket.close();

            oos = null;
            ois = null;
            socket = null;
        } catch (Exception e) {
            System.err.println("failed to cleanup Client");
            e.printStackTrace(System.err);
        }
    }

    public MessageResult sendMessage(Message message) throws InterruptedException {
        CompletableFuture<MessageResult> future = new CompletableFuture<>();
        pendingMessages.put(message.getCorrelationId(), future);
        sourceMessages.put(message.getCorrelationId(), message);

        try {
            oos.writeObject(message);
            return future.get(Constants.KEEPALIVE_DELAY, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            if (listener != null) listener.onError(e);
            return null;
        } finally {
            pendingMessages.remove(message.getCorrelationId());
            sourceMessages.remove(message.getCorrelationId());
        }
    }

    public void sendMessageWithoutResponse(Message message) {
        attempt(() -> oos.writeObject(message));
    }

    private void attempt(ThrowableUtils.ThrowableRunnable action) {
        try {
            action.run();
        } catch (Exception e) {
            if (listener != null) listener.onError(e);
        }
    }

    private <T> T attempt(Callable<T> action) {
        try {
            return action.call();
        } catch (Exception e) {
            if (listener != null) listener.onError(e);
            return null;
        }
    }
}
