package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.WatershipsApplication;
import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.client.state.Resources;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;
import java.util.Random;

public class EndController {
    @FXML
    public ImageView stickman;

    @FXML
    public Label label;

    public void switched() {
        boolean won = GameState.getInstance().index == GameState.getInstance().winnerIndex;
        Platform.runLater(() -> {
            label.setText(won ? "вы победили!" : "вы проиграли!");
            (won ? Resources.SFX.WIN_SFX : Resources.SFX.LOSE_SFX).play();

            int index = (new Random()).nextInt(1, 4);
            stickman.setImage(new Image(Objects.requireNonNull(WatershipsApplication.class.getResourceAsStream("textures/end/" + (won ? "winner_" : "loser_") + index + ".png"))));
        });
    }

    @FXML
    public void onBackPressed() {
        Platform.runLater(() -> {
            Resources.SFX.RESTART_SFX.play();
            GameState.getInstance().reset();
            Client.getInstance().disconnect();
            SceneController.getInstance().activate(SceneController.MENU_SCENE);
        });
    }
}
