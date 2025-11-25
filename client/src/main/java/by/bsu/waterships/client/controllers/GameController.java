package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.client.state.Resources;
import by.bsu.waterships.shared.messages.game.*;
import by.bsu.waterships.shared.types.Board;
import by.bsu.waterships.shared.types.PlayerIndex;
import by.bsu.waterships.shared.types.Point;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class GameController {
    @FXML
    public GridPane opponentGrid;
    @FXML
    public GridPane meGrid;
    @FXML
    public ImageView shipsSnapshot;
    @FXML
    public ImageView theEndBanner;

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

    @FXML
    public Pane opponentShipsContainer;
    @FXML
    public Pane meShipsContainer;

    private Client.ClientCommandListener listener;

    public void switched() {
        createGrids();
        GameState gameState = GameState.getInstance();

        theEndBanner.setVisible(false);
        theEndBanner.setManaged(false);

        opponentImage.setImage(gameState.opponentImage);
        meImage.setImage(gameState.meImage);
        opponentName.setText(gameState.opponentName);
        meName.setText(gameState.meName);
        shipsSnapshot.setImage(gameState.shipsSnapshot);

        for(Node n: meShipsContainer.getChildren()) n.setEffect(null);
        for(Node n: opponentShipsContainer.getChildren()) n.setEffect(null);

        listener = message -> {
            switch (message.getCode()) {
                case GAME_TURN: {
                    PlayerIndex player = ((GameTurnMessage) message).player;
                    Platform.runLater(() -> switchTurn(player == GameState.getInstance().index));
                    break;
                }
                case GAME_FINISH: {
                    GameState.getInstance().winnerIndex = ((GameFinishMessage) message).winner;
                    Platform.runLater(() -> {
                        theEndBanner.setVisible(true);
                        theEndBanner.setManaged(true);
                        Resources.SFX.HORN_SFX.play();
                        new Thread(() -> {
                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            } finally {
                                Platform.runLater(() -> SceneController.getInstance().activate(SceneController.END_SCENE));
                            }
                        }).start();
                    });
                    break;
                }
                case GAME_UPDATE_OPPONENT: {
                    Board.AttackResult result = ((GameUpdateOpponentMessage) message).result;
                    Platform.runLater(() -> {
                        // do not add idle points here
                        setCell(meGrid, result.point(), !result.missed());
                        if (result.destroyedShip() != null) {
                            ImageView destroyedShip = (ImageView) meShipsContainer.getChildren().get(result.destroyedShip().index);
                            destroyedShip.setEffect(new Blend(BlendMode.SRC_ATOP, null, new ColorInput(0, 0, destroyedShip.getFitWidth(), destroyedShip.getFitHeight(), Color.web("#df2024"))));
                        }
                    });
                    break;
                }
            }
        };
        Client.getInstance().addCommandListener(listener);
        Client.getInstance().sendMessageWithoutResponse(new GameReadyMessage());

        Resources.SFX.START_SFX.play();
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

    private void createGrids() {
        meGrid.getChildren().clear();
        opponentGrid.getChildren().clear();
        for (int col = 0; col < 10; col++) {
            for (int row = 0; row < 10; row++) {
                meGrid.add(createCell(false), col, row);
                opponentGrid.add(createCell(true), col, row);
            }
        }
    }

    private static final Background POINTER_BACKGROUND = new Background(new BackgroundFill(Color.web("#77dd7780"), null, null));

    private StackPane createCell(boolean opponent) {
        StackPane cell = new StackPane();
        if (opponent) {
            cell.hoverProperty().addListener(observable -> {
                if (opponentGrid.isDisabled() || !cell.getChildren().isEmpty()) return;
                cell.setBackground(cell.isHover() ? POINTER_BACKGROUND : null);
            });
            cell.setOnMouseClicked(mouseEvent -> {
                if (opponentGrid.isDisabled() || !cell.getChildren().isEmpty()) return;
                Point point = new Point(GridPane.getColumnIndex(cell), GridPane.getRowIndex(cell));
                try {
                    Board.AttackResult result = ((GameAttackMessageResult) Client.getInstance().sendMessage(new GameAttackMessage(point))).result;
                    for (Point idlePoint : result.idlePoints()) setCell(opponentGrid, idlePoint, false);
                    setCell(opponentGrid, point, !result.missed());
                    if (result.destroyedShip() != null) {
                        ImageView destroyedShip = (ImageView) opponentShipsContainer.getChildren().get(result.destroyedShip().index);
                        destroyedShip.setEffect(new Blend(BlendMode.SRC_ATOP, null, new ColorInput(0, 0, destroyedShip.getFitWidth(), destroyedShip.getFitHeight(), Color.web("#df2024"))));
                        Resources.SFX.SHIP_DESTROYED_SFX.play();
                    }

                    if (result.missed()) Resources.SFX.MISS_SFX.play(0.75);
                    else if (result.destroyedShip() == null) Resources.SFX.EXPLOSION_SFX.play(0.75);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return cell;
    }

    private void setCell(GridPane grid, Point point, boolean destroyed) {
        StackPane cell = (StackPane) grid.getChildren().stream().filter(node -> GridPane.getColumnIndex(node) == point.x() && GridPane.getRowIndex(node) == point.y()).findFirst().get();
        cell.getChildren().clear();
        cell.getChildren().add(new ImageView(destroyed ? Resources.DEATH_CELL_TEXTURE : Resources.MISSED_CELL_TEXTURE));
        cell.setBackground(null);
    }
}
