package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.state.Resources;
import by.bsu.waterships.client.state.SceneController;
import javafx.fxml.FXML;

public class AboutController extends SceneController.WatershipsScene {
    @FXML
    public void onBackPressed() {
        Resources.SFX.RESTART_SFX.play();
        SceneController.getInstance().activate(SceneController.MENU_SCENE);
    }
}
