package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.shared.messages.SubmitIntroductionProgressMessageResult;
import by.bsu.waterships.shared.messages.UpdateOpponentIntroductionMessage;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.PlayerInfo;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class IntroduceController {
    @FXML
    public Canvas canvas;

    @FXML
    public ImageView timer;

    @FXML
    public TextField nameField;

    @FXML
    public ImageView opponentImageView;

    @FXML
    public Label opponentName;

    private double prevX, prevY;
    private GraphicsContext gc;
    private Client.ClientCommandListener listener;

    private static final Color DRAW_COLOR = Color.web("#234bb4");
    private static final double DRAW_LINE_WIDTH = 5;
    private static final int NAME_FIELD_MAX_LENGTH = 20;

    public void switched() {
        listener = message -> {
            if (message.getCode() == MessageCode.SUBMIT_INTRODUCTION_PROGRESS) {
                Platform.runLater(() -> {
                    try {
                        SnapshotParameters sp = new SnapshotParameters();
                        sp.setFill(Color.TRANSPARENT);
                        WritableImage wimage = canvas.snapshot(sp, null);
                        RenderedImage rimage = SwingFXUtils.fromFXImage(wimage, null);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(rimage, "png", baos);
                        System.out.println("submit");
                        Client.getInstance().sendMessageWithoutResponse(
                                new SubmitIntroductionProgressMessageResult(
                                        new PlayerInfo(baos.toByteArray(), nameField.getText())
                                )
                        );
                    } catch (Exception ignored) {
                    }
                });
            } else if (message.getCode() == MessageCode.UPDATE_OPPONENT_INTRODUCTION) {
                UpdateOpponentIntroductionMessage uoim = (UpdateOpponentIntroductionMessage) message;
                Platform.runLater(() -> {
                    opponentImageView.setImage(new Image(new ByteArrayInputStream((uoim.info.image))));
                    opponentName.setText(uoim.info.name);
                });
            }
        };
        Client.getInstance().addCommandListener(listener);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > NAME_FIELD_MAX_LENGTH) {
                nameField.setText(newValue.substring(0, NAME_FIELD_MAX_LENGTH));
            }
        });

        gc = canvas.getGraphicsContext2D();
        gc.setStroke(DRAW_COLOR);
        gc.setLineWidth(DRAW_LINE_WIDTH);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        canvas.setOnMousePressed(event -> {
            prevX = event.getX();
            prevY = event.getY();
            gc.beginPath();
            gc.moveTo(prevX, prevY);
            gc.stroke();
        });

        canvas.setOnMouseDragged(event -> {
            gc.lineTo(event.getX(), event.getY());
            gc.stroke();
            prevX = event.getX();
            prevY = event.getY();
        });

        canvas.setOnMouseReleased(event -> {
            gc.closePath();
        });
    }

    public void switchedAway() {
        Client.getInstance().removeCommandListener(listener);
    }
}
