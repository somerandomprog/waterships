package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.shared.Constants;
import by.bsu.waterships.shared.messages.introduction.IntroductionSubmitProgressMessageResult;
import by.bsu.waterships.shared.messages.introduction.IntroductionUpdateOpponentMessage;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.PlayerInfo;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

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

    private AnimationTimer animationTimer;
    private double prevX, prevY;
    private GraphicsContext gc;
    private Client.ClientCommandListener listener;

    private static final Color DRAW_COLOR = Color.BLACK;
    private static final double DRAW_LINE_WIDTH = 5;
    private static final int NAME_FIELD_MAX_LENGTH = 20;

    @FXML
    public void initialize() {
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > NAME_FIELD_MAX_LENGTH) {
                nameField.setText(newValue.substring(0, NAME_FIELD_MAX_LENGTH));
            }
        });
    }

    private Image _lastRenderedImage;

    public void switched() {
        listener = message -> {
            if (message.getCode() == MessageCode.INTRODUCTION_SUBMIT_PROGRESS) {
                Platform.runLater(() -> {
                    try {
                        SnapshotParameters sp = new SnapshotParameters();
                        sp.setFill(Color.TRANSPARENT);
                        WritableImage wimage = canvas.snapshot(sp, null);
                        RenderedImage rimage = SwingFXUtils.fromFXImage(wimage, null);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(rimage, "png", baos);
                        Client.getInstance().sendMessageWithoutResponse(
                                new IntroductionSubmitProgressMessageResult(
                                        new PlayerInfo(baos.toByteArray(), nameField.getText())
                                )
                        );
                        _lastRenderedImage = wimage;
                    } catch (Exception ignored) {
                    }
                });
            } else if (message.getCode() == MessageCode.INTRODUCTION_UPDATE_OPPONENT) {
                IntroductionUpdateOpponentMessage uoim = (IntroductionUpdateOpponentMessage) message;
                Platform.runLater(() -> {
                    opponentImageView.setImage(new Image(new ByteArrayInputStream((uoim.info.image()))));
                    opponentName.setText(uoim.info.name());
                });
            } else if (message.getCode() == MessageCode.INTRODUCTION_END) {
                GameState state = GameState.getInstance();
                state.meName = nameField.getText();
                state.meImage = _lastRenderedImage;
                state.opponentName = opponentName.getText();
                state.opponentImage = opponentImageView.getImage();
                Platform.runLater(() -> SceneController.getInstance().activate(SceneController.ASSEMBLE_BOARD_SCENE));
            }
        };
        Client.getInstance().addCommandListener(listener);

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

        // idk why this has to be in a separate call
        Platform.runLater(() -> {
            Rectangle clipRect = new Rectangle(0, 0, timer.getFitWidth(), timer.getFitHeight());
            timer.setClip(clipRect);
            new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(clipRect.heightProperty(), timer.getFitHeight())),
                    new KeyFrame(Duration.seconds(Constants.INTRODUCTION_DURATION_SECONDS), new KeyValue(clipRect.heightProperty(), 0))
            ).play();
        });
    }

    public void switchedAway() {
        Client.getInstance().removeCommandListener(listener);
        _lastRenderedImage = null;
    }
}
