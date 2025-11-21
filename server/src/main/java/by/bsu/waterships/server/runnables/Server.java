package by.bsu.waterships.server.runnables;

import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.types.PlayerIndex;
import by.bsu.waterships.shared.utils.ThrowableUtils;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    private static Server instance;

    private ServerSocket serverSocket;
    private boolean shouldStop;
    private final List<ClientHandler> handlers = new ArrayList<>();

    private Server() {
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(Constants.PORT);
            serverSocket.setSoTimeout(Constants.ACCEPT_SOCKET_TIMEOUT);

            System.out.println("server started listening on port " + Constants.PORT);
            while (true) {
                Socket socket = ThrowableUtils.nullIfThrows(() -> serverSocket.accept());
                if (socket == null) continue;
                if (handlers.size() >= Constants.MAX_SOCKETS) {
                    System.out.println("rejected connection from " + socket.getInetAddress().getHostAddress() + " (too many sockets)");
                    socket.close();
                    continue;
                }

                int index = handlers.isEmpty() ? 0 : 1;
                ClientHandler handler = new ClientHandler(socket,
                        handlers.isEmpty() ? PlayerIndex.PLAYER_1 : PlayerIndex.PLAYER_2,
                        () -> handlers.remove(index));
                handler.start();
                handlers.add(handler);
                System.out.println("accepted connection from " + socket.getInetAddress().getHostAddress() + "!");

                if (shouldStop) break;
            }
        } catch (Exception e) {
            System.err.println("failed to start server socket on port " + Constants.PORT + " (is it already in use?)");
            e.printStackTrace(System.err);
        }
    }

    public synchronized void stop() {
        this.shouldStop = true;
    }

    public static synchronized Server getInstance() {
        if (instance == null) instance = new Server();
        return instance;
    }
}
