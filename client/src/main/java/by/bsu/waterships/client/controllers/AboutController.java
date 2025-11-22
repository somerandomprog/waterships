package by.bsu.waterships.client.controllers;

import javafx.fxml.FXML;

public class AboutController {
    @FXML
    public void onBackPressed() {
        SceneController.getInstance().activate(SceneController.MENU_SCENE);
    }
}
