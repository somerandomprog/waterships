package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.client.state.Resources;
import by.bsu.waterships.client.state.SceneController;
import javafx.application.Platform;
import javafx.fxml.FXML;

public class InterruptedController extends SceneController.WatershipsScene {
    @FXML
    public void onBackPressed() {
        Platform.runLater(() -> {
            Resources.SFX.RESTART_SFX.play();
            GameState.getInstance().reset();
            Client.getInstance().disconnect();
            SceneController.getInstance().activate(SceneController.MENU_SCENE);
        });
    }

    public void switched() {
        Resources.SFX.INTERRUPTED_SFX.play();
    }
}
