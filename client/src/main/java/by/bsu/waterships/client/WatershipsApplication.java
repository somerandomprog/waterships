package by.bsu.waterships.client;

import by.bsu.waterships.client.controllers.SceneController;
import by.bsu.waterships.client.state.Resources;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class WatershipsApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SceneController controller = SceneController.getInstance(stage);
        controller.add(SceneController.MENU_SCENE, "views/menu-view.fxml");
        controller.add(SceneController.ABOUT_SCENE, "views/about-view.fxml");
        controller.add(SceneController.CONNECT_TO_SERVER_SCENE, "views/connect-to-server-view.fxml");
        controller.add(SceneController.WAIT_SCENE, "views/wait-view.fxml");
        controller.add(SceneController.INTRODUCE_SCENE, "views/introduce-view.fxml");
        controller.add(SceneController.ASSEMBLE_BOARD_SCENE, "views/assemble-board-view.fxml");
        controller.add(SceneController.GAME_SCENE, "views/game-view.fxml");
        controller.add(SceneController.END_SCENE, "views/end-view.fxml");

        Scene scene = new Scene(new Pane(), 1280, 720);
        stage.setResizable(false);
        stage.setTitle("waterships");
        stage.setScene(scene);
        stage.show();

        controller.activate(SceneController.MENU_SCENE);
    }

    public static void main(String[] args) {
        launch();
    }
}