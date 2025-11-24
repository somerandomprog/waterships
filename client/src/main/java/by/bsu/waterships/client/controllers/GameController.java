package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.shared.messages.game.GameReadyMessage;
import by.bsu.waterships.shared.messages.game.GameTurnMessage;
import by.bsu.waterships.shared.types.PlayerIndex;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class GameController {
    @FXML
    public GridPane opponentGrid;
    @FXML
    public GridPane meGrid;
    @FXML
    public ImageView shipsSnapshot;

    @FXML
    public ImageView opponentImage;
    @FXML
    public ImageView meImage;
    @FXML
    public Label opponentName;
    @FXML
    public Label meName;

    @FXML
    public Pane opponentContainer;
    @FXML
    public Pane meContainer;

    private Client.ClientCommandListener listener;

    public void switched() {
        GameState gameState = GameState.getInstance();

        opponentImage.setImage(gameState.opponentImage);
        meImage.setImage(gameState.meImage);
        opponentName.setText(gameState.opponentName);
        meName.setText(gameState.meName);
        shipsSnapshot.setImage(gameState.shipsSnapshot);

        listener = message -> {
            switch (message.getCode()) {
                case GAME_TURN: {
                    PlayerIndex player = ((GameTurnMessage) message).player;
                    Platform.runLater(() -> switchTurn(player == GameState.getInstance().index));
                    break;
                }
                case GAME_FINISH: {
                    break;
                }
                case GAME_UPDATE_OPPONENT: {
                    break;
                }
            }
        };
        Client.getInstance().addCommandListener(listener);
        Client.getInstance().sendMessageWithoutResponse(new GameReadyMessage());
    }

    public void switchedAway() {
        Client.getInstance().removeCommandListener(listener);
    }

    private void switchTurn(boolean me) {
        meContainer.setDisable(me);
        meContainer.setOpacity(me ? 0.75 : 1);
        opponentContainer.setDisable(!me);
        opponentContainer.setOpacity(me ? 1 : 0.75);
    }
}
