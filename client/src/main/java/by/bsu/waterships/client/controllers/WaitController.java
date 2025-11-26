package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.client.state.Resources;
import by.bsu.waterships.client.state.SceneController;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.PlayerIndex;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WaitController extends SceneController.WatershipsScene {
    @FXML
    public Label statusLabel;

    @FXML
    public Label firstPlayerLabel;

    @FXML
    public void initialize() {
        firstPlayerLabel.setManaged(false);
    }

    private Client.ClientCommandListener listener;

    public void switched() {
        Resources.SFX.NOTIFICATION_SFX.play();

        listener = message -> {
            if (message.getCode() == MessageCode.INTRODUCTION_START)
                Platform.runLater(() -> SceneController.getInstance().activate(SceneController.INTRODUCE_SCENE));
        };
        Client.getInstance().addCommandListener(listener);

        if(GameState.getInstance().index == PlayerIndex.PLAYER_1) {
            Platform.runLater(() -> {
                statusLabel.setManaged(false);
                statusLabel.setVisible(false);
                firstPlayerLabel.setManaged(true);
            });
        }
    }

    public void switchedAway() {
        Client.getInstance().removeCommandListener(listener);
    }
}
