package by.bsu.waterships.server.runnables;

import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.messages.PingMessageResult;
import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.MessageResult;
import by.bsu.waterships.shared.types.PlayerIndex;
import by.bsu.waterships.shared.utils.ThrowableUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    interface ClientHandlerListener {
        void onDisconnected();
    }

    private final Socket socket;
    private ClientHandlerListener listener;
    private int retryAttempts = Constants.KEEPALIVE_RETRY_ATTEMPTS;

    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public PlayerIndex index;

    public ClientHandler(Socket socket, PlayerIndex index) {
        this.socket = socket;
        this.index = index;
        setDaemon(true);
    }

    public void setListener(ClientHandlerListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        assert socket != null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            socket.setSoTimeout(Constants.KEEPALIVE_SOCKET_TIMEOUT);

            while (true) {
                Message message = ThrowableUtils.nullIfThrows(() -> (Message) ois.readObject());
                if (message == null) {
                    try {
                        socket.getOutputStream().write(0);
                    } catch (Exception e) {
                        retryAttempts--;
                        socket.setSoTimeout(Constants.KEEPALIVE_SOCKET_TIMEOUT * (Constants.KEEPALIVE_RETRY_ATTEMPTS - retryAttempts));
                        System.out.printf("[%d] seems to be disconnected, %d attempt(s) remaining. new timeout: %d ms\n", index.ordinal() + 1, retryAttempts, socket.getSoTimeout());
                        if (retryAttempts == 0) break;
                    }
                    continue;
                }

                retryAttempts = Constants.KEEPALIVE_RETRY_ATTEMPTS;
                socket.setSoTimeout(Constants.KEEPALIVE_SOCKET_TIMEOUT);
                boolean shouldDisconnect = handleMessage(message);
                if (shouldDisconnect) break;
            }
        } catch (Exception e) {
            System.err.println("something went wrong while talking to client at " + socket.getInetAddress().getHostAddress() + ": " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void printMessage(Message message, boolean fromServer) {
        String prefix = "[" + (fromServer ? "server" : index.ordinal() + 1) + " -> " + (fromServer ? index
                .ordinal() + 1 : "server") + "]";
        System.out.printf("%s %s: %s\n", prefix, message.getCode().name(), message);
    }

    private void respond(MessageResult result) throws IOException {
        printMessage(result, true);
        oos.writeObject(result);
    }

    private boolean handleMessage(Message message) {
        assert message != null;
        printMessage(message, false);

        if (message.getCode() == MessageCode.DISCONNECT) return true;
        try {
            switch (message.getCode()) {
                case PING: {
                    respond(new PingMessageResult());
                    break;
                }
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean disconnected = false;

    private void disconnect() {
        if (disconnected) return;
        try {
            if (oos != null) oos.close();
            if (ois != null) ois.close();
            socket.close();
            listener.onDisconnected();
            System.out.println("disconnected client at " + socket.getInetAddress().getHostAddress());
        } catch (Exception e) {
            System.err.println("failed to disconnect client at " + socket.getInetAddress().getHostAddress());
            e.printStackTrace(System.err);
        } finally {
            interrupt();
            disconnected = true;
        }
    }
}
