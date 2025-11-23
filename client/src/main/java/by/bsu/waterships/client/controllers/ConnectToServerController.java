package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.runnables.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;

import java.util.regex.Pattern;

public class ConnectToServerController {
    private static final Pattern IP_ADDRESS_REGEX = Pattern.compile("^(((?!25?[6-9])[12]\\d|[1-9])?\\d\\.?\\b){4}$");
    private static final int CONNECT_ANIMATION_DELAY = 250;

    private boolean connecting = false;

    @FXML
    public TextField ipField;

    @FXML
    public Label errorLabel;

    @FXML
    public Button connectButton;

    @FXML
    public void onConnectPressed() {
        if (!IP_ADDRESS_REGEX.matcher(ipField.getText()).matches()) {
            error("что-то не похоже на ip адрес...");
            return;
        }

        error("");
        setConnecting(true);
        new Thread(this::animateButton).start();

        Client client = Client.getInstance(ipField.getText());
        client.setListener(new Client.ClientListener() {
            @Override
            public void onConnect() {
                SceneController.getInstance().activate(SceneController.WAIT_SCENE);
            }

            @Override
            public void onDisconnect() {
                setConnecting(false);
                Platform.runLater(() -> error("не удалось проверить соединение с сервером"));
                System.err.println("got disconnected from " + ipField.getText() + " (did the ping message fail?)");
            }

            @Override
            public void onError(Exception e) {
                setConnecting(false);
                Platform.runLater(() -> error("при подключении к серверу произошла ошибка"));
                System.err.println("failed to connect to " + ipField.getText() + ": " + e.getMessage());
            }
        });
        client.start();
    }

    public void switchedAway() {
        Client.getInstance().setListener(null);
        setConnecting(false);
        error("");
    }

    @FXML
    public void onBackPressed() {
        SceneController.getInstance().activate(SceneController.MENU_SCENE);
    }

    @FXML
    public void initialize() {
        ipField.textProperty().addListener((observable, oldValue, newValue) -> error(""));
    }

    private void error(String text) {
        errorLabel.setText(text);
    }

    private synchronized boolean isConnecting() {
        return connecting;
    }

    private synchronized void setConnecting(boolean connecting) {
        this.connecting = connecting;
        connectButton.setDisable(connecting);
    }

    private void animateButton() {
        int current = 1;
        while (isConnecting()) {
            connectButton.getStyleClass().clear();
            connectButton.getStyleClass().add("s" + current);
            try {
                Thread.sleep(CONNECT_ANIMATION_DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            current = current + 1 == 4 ? 1 : current + 1;
        }
        connectButton.getStyleClass().clear();
    }
}
