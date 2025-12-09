package by.bsu.waterships.server.runnables;

import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.protocol.*;
import by.bsu.waterships.shared.protocol.results.ActionResultMessage;
import by.bsu.waterships.shared.protocol.results.IntroductionSubmitProgressResultMessage;
import by.bsu.waterships.shared.types.*;
import by.bsu.waterships.shared.utils.ThrowableUtils;
import by.bsu.waterships.shared.utils.XmlUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ClientHandler extends Thread {
    public interface ClientHandlerListener {
        void onDisconnected();

        void onConnectionEstablished();
    }

    private final Socket socket;
    private ClientHandlerListener listener;
    private int retryAttempts = Constants.KEEPALIVE_RETRY_ATTEMPTS;

    private PrintStream output;
    private Scanner input;

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
            output = new PrintStream(socket.getOutputStream());
            input = new Scanner(socket.getInputStream());
            socket.setSoTimeout(Constants.KEEPALIVE_DELAY);
            send(new HandshakeMessage(index));

            while (true) {
                String message = ThrowableUtils.nullIfThrows(() -> input.nextLine());
                try {
                    if (message == null) throw new Exception("no message received");
                    if (!message.startsWith("@")) throw new Exception("message didn't start with \"@\"");

                    String className = message.substring(1);
                    String data = input.nextLine();
                    XmlUtils.XmlResult parseResult = XmlUtils.unmarshal(className, data);
                    if (!parseResult.success()) throw new Exception(parseResult.error());

                    boolean shouldDisconnect = handleMessage((ActionMessage) parseResult.data());
                    if (shouldDisconnect) break;
                } catch (Exception e) {
                    System.out.println("pinging [" + (index.ordinal() + 1) + "]: " + e.getMessage());
                    try {
                        send(new ActionMessage("ping"));
                        retryAttempts--;
                        socket.setSoTimeout(Constants.KEEPALIVE_DELAY * (Constants.KEEPALIVE_RETRY_ATTEMPTS - retryAttempts));
                        if (retryAttempts < Constants.KEEPALIVE_RETRY_ATTEMPTS - 1)
                            System.out.printf("[%d] seems to be disconnected, %d attempt(s) remaining. new timeout: %d ms\n", index.ordinal() + 1, retryAttempts, socket.getSoTimeout());
                        if (retryAttempts == 0) break;
                    } catch (Exception ignored) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("something went wrong while talking to client at " + socket.getInetAddress().getHostAddress() + ": " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void printMessage(ActionMessage message, boolean fromServer) {
        String prefix = "[" + (fromServer ? "server" : index.ordinal() + 1) + " -> " + (fromServer ? index
                .ordinal() + 1 : "server") + "]";
        System.out.printf("%s %s: %s\n", prefix, message.getAction(), message);
    }

    public void send(ActionMessage message) throws IOException {
        printMessage(message, true);
        output.println("@" + message.getClass().getSimpleName());
        output.println((String) XmlUtils.marshal(message).data());
    }

    private boolean handleMessage(ActionMessage message) {
        assert message != null;
        printMessage(message, false);

        switch (message.getAction()) {
            case "disconnect":
                return true;
            case "ping_result": {
                retryAttempts = Constants.KEEPALIVE_RETRY_ATTEMPTS;
                try {
                    socket.setSoTimeout(Constants.KEEPALIVE_DELAY);
                } catch (SocketException ignored) {
                }
                return false;
            }
            case "introduction_submit_progress_result": {
                try {
                    getOpponentHandler().send(new IntroductionUpdateOpponentMessage(((IntroductionSubmitProgressResultMessage) message).info));
                } catch (IOException ignored) {
                }
                return false;
            }
        }

        try {
            switch (message.getAction()) {
                case "ping": {
                    send(new ActionResultMessage("ping_result", message.getCorrelationId()));
                    break;
                }

                // assembly
                case "assembly_place_ship": {
                    int total = ((AssemblyPlacedShipMessage) message).total;
                    getOpponentHandler().send(new AssemblyUpdateOpponentMessage(total));
                    break;
                }
                case "assembly_ready": {
                    Board board = ((AssemblyReadyMessage) message).board;
                    Server.getInstance().getCurrentSession().playerAssembledBoard(index, board);
                    break;
                }

                // game
                case "game_ready": {
                    Server.getInstance().getCurrentSession().playerReady(index);
                    break;
                }
                case "game_attack": {
                    Point point = ((GameAttackMessage) message).where;
                    Server.getInstance().getCurrentSession().handleAttack(message, index, point);
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
            if (output != null) output.close();
            if (input != null) input.close();
            socket.close();
            System.out.println("disconnected client at " + socket.getInetAddress().getHostAddress());
        } catch (Exception e) {
            System.err.println("failed to disconnect client at " + socket.getInetAddress().getHostAddress());
            e.printStackTrace(System.err);
        } finally {
            disconnected = true;
            listener.onDisconnected();
            interrupt();
        }
    }

    private ClientHandler getOpponentHandler() {
        return Server.getInstance().getSocket(index == PlayerIndex.PLAYER_1 ? PlayerIndex.PLAYER_2 : PlayerIndex.PLAYER_1);
    }
}
