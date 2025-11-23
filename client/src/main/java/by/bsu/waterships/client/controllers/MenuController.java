package by.bsu.waterships.client.controllers;

import javafx.fxml.FXML;

public class MenuController {
    @FXML
    public void onPlayPressed() {
        SceneController.getInstance().activate(SceneController.CONNECT_TO_SERVER_SCENE);
    }

    @FXML
    public void onAboutPressed() {
        SceneController.getInstance().activate(SceneController.ABOUT_SCENE);
    }
}