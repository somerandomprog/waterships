package by.bsu.waterships.client.runnables;


import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.protocol.ActionMessage;
import by.bsu.waterships.shared.protocol.HandshakeMessage;
import by.bsu.waterships.shared.protocol.results.ActionResultMessage;
import by.bsu.waterships.shared.utils.ThrowableUtils;
import by.bsu.waterships.shared.utils.XmlUtils;
import javafx.application.Platform;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.*;

public class Client extends Thread {
    public interface ClientListener {
        void onConnect();

        void onDisconnect();

        void onError(Exception e);
    }

    public interface ClientCommandListener {
        void onMessage(ActionMessage message) throws Exception;
    }

    private static Client instance;

    private final ConcurrentHashMap<String, ActionMessage> sourceMessages = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<ActionResultMessage>> pendingMessages = new ConcurrentHashMap<>();

    private ClientListener listener;
    private ConcurrentLinkedQueue<ClientCommandListener> commandListeners = new ConcurrentLinkedQueue<>();
    private String host;
    private boolean connected;

    private Socket socket;
    private PrintStream output;
    private Scanner input;

    private Client() {
    }

    private Client(String host) {
        this.host = host;
        addCommandListener(message -> {
            if (message.getAction().equals("ping")) {
                sendMessageWithoutResponse(new ActionResultMessage("ping_result", message.getCorrelationId()));
            } else if (message.getAction().equals("handshake")) {
                GameState.getInstance().index = ((HandshakeMessage) message).getIndex();
                sendMessageWithoutResponse(new ActionMessage("handshake_result"));
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
            output = new PrintStream(socket.getOutputStream());
            input = new Scanner(socket.getInputStream());

            System.out.println("initialized socket to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    if(!input.hasNextLine()) continue;
                    String message = ThrowableUtils.nullIfThrows(() -> input.nextLine());
                    System.out.println(message);
                    if (!message.startsWith("@")) continue;
                    String className = message.substring(1);
                    String data = input.nextLine();
                    System.out.println(data);
                    XmlUtils.XmlResult parseResult = XmlUtils.unmarshal(className, data);
                    if (!parseResult.success()) {
                        System.err.println(className + ": " + data);
                        System.err.println(parseResult.error());
                    }

                    ActionMessage parsedMessage = (ActionMessage) parseResult.data();
                    if (parsedMessage instanceof ActionResultMessage && pendingMessages.containsKey(parsedMessage.getCorrelationId())) {
                        System.out.printf("[%s -> %s] %s\n", sourceMessages.get(parsedMessage.getCorrelationId()).getAction(), parsedMessage.getAction(), message);
                        pendingMessages.get(parsedMessage.getCorrelationId()).complete((ActionResultMessage) parsedMessage);
                        pendingMessages.remove(parsedMessage.getCorrelationId());
                        sourceMessages.remove(parsedMessage.getCorrelationId());
                    } else {
                        System.out.printf("[%s] %s\n", parsedMessage.getAction(), message);
                        for (ClientCommandListener commandListener : commandListeners)
                            commandListener.onMessage(parsedMessage);
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
            sendMessageWithoutResponse(new ActionMessage("disconnect"));
            connected = false;
            cleanup();
            if (listener != null) listener.onDisconnect();
            interrupt();
        });
    }

    private void cleanup() {
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (socket != null && !socket.isClosed()) socket.close();
            socket = null;
        } catch (Exception e) {
            System.err.println("failed to cleanup Client");
            e.printStackTrace(System.err);
        }
    }

    public ActionResultMessage sendMessage(ActionMessage message) throws InterruptedException {
        CompletableFuture<ActionResultMessage> future = new CompletableFuture<>();
        pendingMessages.put(message.getCorrelationId(), future);
        sourceMessages.put(message.getCorrelationId(), message);

        try {
            output.println("@" + message.getClass().getName());
            output.println((String) XmlUtils.marshal(message).data());
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

    public void sendMessageWithoutResponse(ActionMessage message) {
        attempt(() -> {
            output.println("@" + message.getClass().getName());
            output.println((String) XmlUtils.marshal(message).data());
        });
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
