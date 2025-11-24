package by.bsu.waterships.server.runnables;

import by.bsu.waterships.server.logic.Game;
import by.bsu.waterships.server.logic.GameState;
import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.messages.introduction.IntroductionStartMessage;
import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.PlayerIndex;
import by.bsu.waterships.shared.utils.ThrowableUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    private static Server instance;

    private ServerSocket serverSocket;
    private boolean shouldStop;
    private final List<ClientHandler> handlers = new ArrayList<>();
    private Game currentSession;

    private Server() {
    }

    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(Constants.PORT);
            serverSocket.setSoTimeout(Constants.ACCEPT_SOCKET_TIMEOUT);

            System.out.println("server started listening on port " + Constants.PORT);
            System.out.println("local address: " + InetAddress.getLocalHost().getHostAddress());

            while (!shouldStop) {
                Socket socket = ThrowableUtils.nullIfThrows(() -> serverSocket.accept());
                if (socket == null) continue;
                if (handlers.size() >= Constants.MAX_SOCKETS) {
                    System.out.println("rejected connection from " + socket.getInetAddress().getHostAddress() + " (too many sockets)");
                    socket.close();
                    continue;
                }
                System.out.println("accepted connection from " + socket.getInetAddress().getHostAddress());

                if (handlers.isEmpty()) {
                    System.out.println("beginning new game session");
                    currentSession = new Game();
                }

                ClientHandler handler = new ClientHandler(socket,
                        handlers.isEmpty() ? PlayerIndex.PLAYER_1 : PlayerIndex.PLAYER_2);
                handler.setListener(() -> {
                    System.out.println("disconnected socket [" + (handler.index.ordinal() + 1) + "]");
                    if (handler.index == PlayerIndex.PLAYER_1 && handlers.size() == Constants.MAX_SOCKETS) {
                        System.out.println("reassigning sockets. socket [2] is now [1]");
                        handlers.get(1).index = PlayerIndex.PLAYER_1;
                    }
                    handlers.remove(handler.index.ordinal());

                    if (handlers.isEmpty()) {
                        System.out.println("destroying current game session (all players left)");
                        currentSession.dispose();
                        currentSession = null;
                    }
                });
                handler.start();
                handlers.add(handler);

                if (handlers.size() == 2) {
                    System.out.println("beginning introduction");
                    currentSession.setState(GameState.INTRODUCTION);
                    broadcast(new IntroductionStartMessage(Constants.INTRODUCTION_DURATION_SECONDS));
                }
            }
        } catch (Exception e) {
            System.err.println("failed to start server socket on port " + Constants.PORT + " (is it already in use?)");
            e.printStackTrace(System.err);
        }
    }

    public void broadcast(Message message) {
        for (ClientHandler handler : handlers) {
            try {
                handler.send(message);
            } catch (IOException e) {
                System.err.println("failed to broadcast " + message + " to [" + handler.index + "]: " + e.getMessage());
            }
        }
    }

    public ClientHandler getSocket(PlayerIndex index) {
        return handlers.stream().filter(ch -> ch.index == index).findFirst().orElse(null);
    }

    public synchronized void stop() {
        this.shouldStop = true;
    }

    public static synchronized Server getInstance() {
        if (instance == null) instance = new Server();
        return instance;
    }

    public Game getCurrentSession() {
        return currentSession;
    }
}
