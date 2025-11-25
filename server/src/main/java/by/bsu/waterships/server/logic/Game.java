package by.bsu.waterships.server.logic;

import by.bsu.waterships.server.runnables.Server;
import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.messages.game.*;
import by.bsu.waterships.shared.messages.introduction.IntroductionEndMessage;
import by.bsu.waterships.shared.messages.introduction.IntroductionSubmitProgressMessage;
import by.bsu.waterships.shared.types.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class Game {
    private GameState state;
    private PlayerIndex turn;

    private final ConcurrentHashMap<PlayerIndex, Board> boards = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<PlayerIndex, Boolean> ready = new ConcurrentHashMap<>();

    public Game() {
        this.state = GameState.WAITING_FOR_PLAYERS;
    }

    public void setState(GameState state) {
        this.state = state;
        switch (state) {
            case INTRODUCTION: {
                introductionPollingThread = new Thread(() -> {
                    int total = 0;
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Thread.sleep(Constants.INTRODUCTION_REQUEST_DELAY);
                            total += Constants.INTRODUCTION_REQUEST_DELAY;
                            if (total >= Constants.INTRODUCTION_DURATION_SECONDS * 1000) {
                                Server.getInstance().broadcast(new IntroductionEndMessage());
                                setState(GameState.ASSEMBLE_BOARD);
                            } else Server.getInstance().broadcast(new IntroductionSubmitProgressMessage());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
                introductionPollingThread.start();
                break;
            }
            case ASSEMBLE_BOARD: {
                introductionPollingThread.interrupt();
                introductionPollingThread = null;
                break;
            }
            case PLAYING: {
                Server.getInstance().broadcast(new GameBeginMessage());
                break;
            }
        }
    }

    public void dispose() {
        if (introductionPollingThread != null) introductionPollingThread.interrupt();
    }

    public GameState getState() {
        return state;
    }

    public void playerAssembledBoard(PlayerIndex which, Board board) {
        boards.put(which, board);
        if (boards.size() == 2) setState(GameState.PLAYING);
    }

    public void playerReady(PlayerIndex which) {
        ready.put(which, true);
        if (ready.size() == 2) {
            System.out.println("beginning the game!");
            switchTurn(PlayerIndex.PLAYER_1);
        }
    }

    public void handleAttack(Message attackerMessage, PlayerIndex attacker, Point point) {
        PlayerIndex opponent = attacker == PlayerIndex.PLAYER_1 ? PlayerIndex.PLAYER_2 : PlayerIndex.PLAYER_1;

        Board opponentBoard = boards.get(opponent);
        Board.AttackResult result = opponentBoard.attack(point);

        try {
            Server.getInstance().getSocket(attacker).send(attackerMessage.respond(new GameAttackMessageResult(result)));
            Server.getInstance().getSocket(opponent).send(new GameUpdateOpponentMessage(result));
            if (result.missed()) switchTurn(opponent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (opponentBoard.allShipsDestroyed())
            Server.getInstance().broadcast(new GameFinishMessage(attacker));
    }

    private void switchTurn(PlayerIndex index) {
        turn = index;
        Server.getInstance().broadcast(new GameTurnMessage(index));
    }

    private Thread introductionPollingThread;
}
