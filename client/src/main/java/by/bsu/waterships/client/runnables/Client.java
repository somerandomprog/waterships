package by.bsu.waterships.client.runnables;


import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.protocol.ActionMessage;
import by.bsu.waterships.shared.protocol.HandshakeMessage;
import by.bsu.waterships.shared.protocol.results.ActionResultMessage;
import by.bsu.waterships.shared.utils.NullUtils;
import by.bsu.waterships.shared.utils.ThrowableUtils;
import by.bsu.waterships.shared.utils.XmlUtils;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
    private DataOutputStream output;
    private DataInputStream input;

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
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());

            System.out.println("initialized socket to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ActionMessage message = ThrowableUtils.nullIfThrows(this::tryReceiveMessage);
                    if (message == null) continue;
                    if (message instanceof ActionResultMessage && pendingMessages.containsKey(message.getCorrelationId())) {
                        System.out.printf("[%s -> %s] %s\n", sourceMessages.get(message.getCorrelationId()).getAction(), message.getAction(), message);
                        pendingMessages.get(message.getCorrelationId()).complete((ActionResultMessage) message);
                        pendingMessages.remove(message.getCorrelationId());
                        sourceMessages.remove(message.getCorrelationId());
                    } else {
                        System.out.printf("[%s] %s\n", message.getAction(), message);
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

    private ActionMessage tryReceiveMessage() throws IOException {
        int classNameLength = input.readInt();
        byte[] classNameBytes = new byte[classNameLength];
        input.readFully(classNameBytes, 0, classNameLength);
        String className = new String(classNameBytes, StandardCharsets.UTF_8);

        int payloadLength = input.readInt();
        byte[] payloadBytes = new byte[payloadLength];
        input.readFully(payloadBytes, 0, payloadLength);
        String payload = new String(payloadBytes, StandardCharsets.UTF_8);

        System.out.printf("\t<< payloadLength: %d | className: %s\n", payloadLength, className);
        if (!className.contains("IntroductionSubmitProgressResultMessage") && !className.contains("IntroductionUpdateOpponentMessage"))
            System.out.println("\t" + payload);
        else System.out.println("\t<< not outputting introduction payload (too long) >:(");

        XmlUtils.XmlResult parseResult = XmlUtils.unmarshal(className, payload);
        if (!parseResult.success()) {
            System.err.println(parseResult.error());
            return null;
        }
        return (ActionMessage) parseResult.data();
    }

    private void tryWriteMessage(ActionMessage message) throws IOException {
        XmlUtils.XmlResult result = XmlUtils.marshal(message);
        if (!result.success()) {
            System.err.println(message);
            System.err.println(result.error());
            return;
        }

        String className = message.getClass().getName();
        String payload = (String) result.data();

        System.out.printf("\t>> payloadLength: %d | className: %s\n", payload.length(), className);
        if (!className.contains("IntroductionUpdateOpponentMessage") && !className.contains("IntroductionSubmitProgressResultMessage"))
            System.out.println("\t" + payload);
        else System.out.println("\t>> not outputting introduction payload (too long) >:(");

        output.writeInt(className.length());
        output.writeBytes(className);
        output.writeInt(payload.length());
        output.writeBytes(payload);
        output.flush();
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
            tryWriteMessage(message);
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
        attempt(() -> tryWriteMessage(message));
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
