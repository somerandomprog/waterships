package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.WatershipsApplication;
import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.client.state.Resources;
import by.bsu.waterships.shared.messages.game.*;
import by.bsu.waterships.shared.types.Board;
import by.bsu.waterships.shared.types.PlayerIndex;
import by.bsu.waterships.shared.types.Point;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Objects;

public class GameController {
    @FXML
    public GridPane opponentGrid;
    @FXML
    public GridPane meGrid;
    @FXML
    public ImageView shipsSnapshot;

    @FXML
    public ImageView opponentImage;
    @FXML
    public ImageView meImage;
    @FXML
    public Label opponentName;
    @FXML
    public Label meName;

    @FXML
    public Pane opponentContainer;
    @FXML
    public Pane meContainer;
    @FXML
    public Label turnLabel;

    private Client.ClientCommandListener listener;

    public void switched() {
        createOpponentGrid();
        GameState gameState = GameState.getInstance();

        opponentImage.setImage(gameState.opponentImage);
        meImage.setImage(gameState.meImage);
        opponentName.setText(gameState.opponentName);
        meName.setText(gameState.meName);
        shipsSnapshot.setImage(gameState.shipsSnapshot);

        listener = message -> {
            switch (message.getCode()) {
                case GAME_TURN: {
                    PlayerIndex player = ((GameTurnMessage) message).player;
                    Platform.runLater(() -> switchTurn(player == GameState.getInstance().index));
                    break;
                }
                case GAME_FINISH: {
                    break;
                }
                case GAME_UPDATE_OPPONENT: {
                    Board.AttackResult result = ((GameUpdateOpponentMessage)message).result;
                    break;
                }
            }
        };
        Client.getInstance().addCommandListener(listener);
        Client.getInstance().sendMessageWithoutResponse(new GameReadyMessage());
    }

    public void switchedAway() {
        Client.getInstance().removeCommandListener(listener);
    }

    private void switchTurn(boolean me) {
        meContainer.setDisable(me);
        meContainer.setOpacity(me ? 0.75 : 1);
        opponentContainer.setDisable(!me);
        opponentContainer.setOpacity(me ? 1 : 0.75);
        turnLabel.setText(me ? "вы\nходите!" : "ходит\nсоперник");
    }

    private void createOpponentGrid() {
        opponentGrid.getChildren().clear();
        for (int col = 0; col < 10; col++)
            for (int row = 0; row < 10; row++)
                opponentGrid.add(createOpponentCell(), col, row);
    }

    private static final Background POINTER_BACKGROUND = new Background(new BackgroundFill(Color.web("#77dd7780"), null, null));

    private Pane createOpponentCell() {
        Pane cell = new Pane();
        cell.hoverProperty().addListener(observable -> {
            if (opponentGrid.isDisabled()) return;
            cell.setBackground(cell.isHover() ? POINTER_BACKGROUND : null);
        });
        cell.setOnMouseClicked(mouseEvent -> {
            if (opponentGrid.isDisabled()) return;
            Point point = new Point(GridPane.getColumnIndex(cell), GridPane.getRowIndex(cell));
            try {
                Board.AttackResult result = ((GameAttackMessageResult) Client.getInstance().sendMessage(new GameAttackMessage(point))).result;
                for (Point missedPoint : result.markMissed()) setOpponentCell(missedPoint, false);
                setOpponentCell(point, !result.missed());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return cell;
    }

    private void setOpponentCell(Point point, boolean destroyed) {
        opponentGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == point.x() && GridPane.getRowIndex(node) == point.y());
        opponentGrid.add(new ImageView(destroyed ? Resources.DEATH_CELL_TEXTURE : Resources.MISSED_CELL_TEXTURE), point.x(), point.y());
    }

    private void setMeCell(Point point, boolean destroyed) {
        meGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == point.x() && GridPane.getRowIndex(node) == point.y());
        meGrid.add(new ImageView(destroyed ? Resources.DEATH_CELL_TEXTURE : Resources.MISSED_CELL_TEXTURE), point.x(), point.y());
    }
}
