package by.bsu.waterships.client;

import by.bsu.waterships.client.controllers.SceneController;
import by.bsu.waterships.client.runnables.Client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class WatershipsApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Resources.TITLE_FONT = Font.loadFont(WatershipsApplication.class.getResourceAsStream("fonts/briston.otf"), 48);
        Resources.BODY_FONT = Font.loadFont(WatershipsApplication.class.getResourceAsStream("fonts/klyakson.ttf"), 48);

        SceneController controller = SceneController.getInstance(stage);
        controller.add(SceneController.MENU_SCENE, "views/menu-view.fxml");
        controller.add(SceneController.ABOUT_SCENE, "views/about-view.fxml");

        stage.setResizable(false);
        stage.setTitle("waterships");
        stage.setScene(new Scene(new Pane(), 1280, 720));
        stage.show();

        controller.activate(SceneController.MENU_SCENE);
    }

    public static void main(String[] args) throws UnknownHostException {
        Client.getInstance().configure(InetAddress.getLocalHost().getHostAddress()).start();
        launch();
    }
}