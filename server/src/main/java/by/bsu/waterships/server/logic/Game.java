package by.bsu.waterships.server.logic;

import by.bsu.waterships.server.runnables.Server;
import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.messages.SubmitIntroductionProgressMessage;

public class Game {
    private GameState state;

    public Game() {
        this.state = GameState.WAITING_FOR_PLAYERS;
    }

    public void setState(GameState state) {
        this.state = state;
        switch (state) {
            case INTRODUCTION: {
                introductionPollingThread = new Thread(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Thread.sleep(Constants.INTRODUCTION_REQUEST_DELAY);
                            Server.getInstance().broadcast(new SubmitIntroductionProgressMessage());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
                introductionPollingThread.start();
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

    private Thread introductionPollingThread;
}
