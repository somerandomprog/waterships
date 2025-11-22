package by.bsu.waterships.client;

import by.bsu.waterships.client.runnables.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

        FXMLLoader fxmlLoader = new FXMLLoader(WatershipsApplication.class.getResource("views/menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("waterships");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws UnknownHostException {
        Client.getInstance().configure(InetAddress.getLocalHost().getHostAddress()).start();
        launch();
    }
}