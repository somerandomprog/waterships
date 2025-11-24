package by.bsu.waterships.server.logic;

import by.bsu.waterships.server.runnables.Server;
import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.messages.game.GameBeginMessage;
import by.bsu.waterships.shared.messages.game.GameTurnMessage;
import by.bsu.waterships.shared.messages.introduction.IntroductionEndMessage;
import by.bsu.waterships.shared.messages.introduction.IntroductionSubmitProgressMessage;
import by.bsu.waterships.shared.types.Board;
import by.bsu.waterships.shared.types.PlayerIndex;
import by.bsu.waterships.shared.types.PlayerInfo;

import java.util.concurrent.ConcurrentHashMap;

public class Game {
    private GameState state;
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
            Server.getInstance().broadcast(new GameTurnMessage(PlayerIndex.PLAYER_1));
        }
    }

    private Thread introductionPollingThread;
}
