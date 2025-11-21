package by.bsu.waterships.client.runnables;


import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.messages.DisconnectMessage;
import by.bsu.waterships.shared.messages.PingMessage;
import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageResult;
import by.bsu.waterships.shared.utils.ThrowableUtils;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Client extends Thread {
    interface ClientListener {
        void onConnect();

        void onDisconnect();

        void onError(Exception e);
    }

    private static Client instance;

    private ClientListener listener;
    private final String host;
    private boolean connected;

    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private Client(String host) {
        this.host = host;
    }

    public void setListener(ClientListener listener) {
        this.listener = listener;
    }

    public static Client getInstance(String host) {
        if (instance == null || !instance.host.equals(host)) instance = new Client(host);
        return instance;
    }

    @Override
    public void run() {
        attempt(() -> {
            socket = new Socket(host, Constants.PORT);
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("initialized socket to " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
            ping();
        });
    }

    private void ping() {
        System.out.print("testing connection with the server... ");
        MessageResult result = sendMessage(new PingMessage());
        if (result == null || result.getError() != null) {
            System.out.println("NOT OK");
            disconnect();
        } else System.out.println("OK");
    }

    public void disconnect() {
        if (!connected) return;
        attempt(() -> {
            oos.writeObject(new DisconnectMessage());
            connected = false;
        });
    }

    public MessageResult sendMessage(Message message) {
        return attempt(() -> {
            oos.writeObject(message);
            return (MessageResult) ois.readObject();
        });
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
