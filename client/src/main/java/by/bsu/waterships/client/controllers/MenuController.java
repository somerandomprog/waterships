package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.state.Resources;
import javafx.fxml.FXML;

public class MenuController {
    @FXML
    public void onPlayPressed() {
        Resources.SFX.PAPER_SFX.play();
        SceneController.getInstance().activate(SceneController.CONNECT_TO_SERVER_SCENE);
    }

    @FXML
    public void onAboutPressed() {
        Resources.SFX.PAPER_SFX.play();
        SceneController.getInstance().activate(SceneController.ABOUT_SCENE);
    }
}