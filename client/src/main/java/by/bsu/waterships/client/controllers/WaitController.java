package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.shared.messages.GetPlayerIndexMessage;
import by.bsu.waterships.shared.messages.GetPlayerIndexMessageResult;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.MessageResult;
import by.bsu.waterships.shared.types.PlayerIndex;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WaitController {
    @FXML
    public Label statusLabel;

    @FXML
    public Label firstPlayerLabel;

    @FXML
    public void initialize() {
        firstPlayerLabel.setManaged(false);
    }

    private Client.ClientCommandListener listener;
    private Thread receivePlayerIndexThread;

    public void switched() {
        listener = message -> {
            if (message.getCode() == MessageCode.INTRODUCTION_START)
                Platform.runLater(() -> SceneController.getInstance().activate(SceneController.INTRODUCE_SCENE));
        };
        Client.getInstance().addCommandListener(listener);

        receivePlayerIndexThread = new Thread(() -> {
            try {
                MessageResult result = Client.getInstance().sendMessage(new GetPlayerIndexMessage());
                if (result instanceof GetPlayerIndexMessageResult playerIndexMessageResult) {
                    if (playerIndexMessageResult.index == PlayerIndex.PLAYER_1) Platform.runLater(() -> {
                        statusLabel.setManaged(false);
                        statusLabel.setVisible(false);
                        firstPlayerLabel.setManaged(true);
                    });
                } else throw new Exception("failed to get player index from server");
            } catch (InterruptedException e) {
                receivePlayerIndexThread.interrupt();
            } catch (Exception e) {
                statusLabel.setText(e.getMessage());
                e.printStackTrace(System.err);
            }
        });
        receivePlayerIndexThread.start();
    }

    public void switchedAway() {
        receivePlayerIndexThread.interrupt();
        Client.getInstance().removeCommandListener(listener);
    }
}
