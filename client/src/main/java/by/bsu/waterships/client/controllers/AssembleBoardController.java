package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.runnables.Client;
import by.bsu.waterships.client.state.GameState;
import by.bsu.waterships.shared.messages.GetPlayerIndexMessage;
import by.bsu.waterships.shared.messages.GetPlayerIndexMessageResult;
import by.bsu.waterships.shared.messages.assembly.AssemblyPlacedShipMessage;
import by.bsu.waterships.shared.messages.assembly.AssemblyReadyMessage;
import by.bsu.waterships.shared.messages.assembly.AssemblyUpdateOpponentMessage;
import by.bsu.waterships.shared.types.Board;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.Point;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

import static by.bsu.waterships.shared.utils.NullUtils.coalesce;

public class AssembleBoardController {
    @FXML
    public GridPane boardGrid;
    @FXML
    public Pane shipsContainer;
    @FXML
    public Button letsgo;
    @FXML
    public Label opponentProgress;
    @FXML
    public ImageView opponentImage;
    @FXML
    public Label opponentName;

    @FXML
    public ImageView ship4;
    @FXML
    public ImageView ship3_1;
    @FXML
    public ImageView ship3_2;
    @FXML
    public ImageView ship2_1;
    @FXML
    public ImageView ship2_2;
    @FXML
    public ImageView ship2_3;
    @FXML
    public ImageView ship1_1;
    @FXML
    public ImageView ship1_2;
    @FXML
    public ImageView ship1_3;
    @FXML
    public ImageView ship1_4;

    private boolean ready;

    @FXML
    public void onReadyPressed() {
        sendBoard();
        ready = true;
        letsgo.getStyleClass().add("waiting");
        letsgo.setDisable(true);
    }

    private void sendBoard() {
        Board board = new Board();
        for (ImageView ship : allShips) {
            if (ship.getParent() != boardGrid) continue;
            int col = coalesce(GridPane.getColumnIndex(ship), 0);
            int row = coalesce(GridPane.getRowIndex(ship), 0);
            int length = (int) ship.getUserData();
            boolean isVertical = ship.getRotate() != 0;
            board.addShip(new Point(col, row), length, isVertical);
        }
        Client.getInstance().sendMessageWithoutResponse(new AssemblyReadyMessage(board));
    }

    private List<ImageView> allShips;
    private Client.ClientCommandListener commandListener;

    @FXML
    public void initialize() {
        allShips = Arrays.asList(
                ship4, ship3_1, ship3_2, ship2_1, ship2_2, ship2_3,
                ship1_1, ship1_2, ship1_3, ship1_4
        );

        initializeGrid();
        setupShip(ship4, 4);
        setupShip(ship3_1, 3);
        setupShip(ship3_2, 3);
        setupShip(ship2_1, 2);
        setupShip(ship2_2, 2);
        setupShip(ship2_3, 2);
        setupShip(ship1_1, 1);
        setupShip(ship1_2, 1);
        setupShip(ship1_3, 1);
        setupShip(ship1_4, 1);
    }

    public void switched() {
        ready = false;
        letsgo.setDisable(true);
        opponentProgress.setText("0/10");
        opponentName.setText(GameState.getInstance().opponentName);
        opponentImage.setImage(GameState.getInstance().opponentImage);

        commandListener = message -> {
            if (message.getCode() == MessageCode.ASSEMBLY_UPDATE_OPPONENT) {
                int total = ((AssemblyUpdateOpponentMessage) message).total;
                Platform.runLater(() -> opponentProgress.setText(total + "/10"));
            } else if (message.getCode() == MessageCode.GAME_BEGIN) {
                Platform.runLater(() -> {
                    SnapshotParameters sp = new SnapshotParameters();
                    sp.setFill(Color.TRANSPARENT);
                    GameState.getInstance().shipsSnapshot = boardGrid.snapshot(sp, null);
                    SceneController.getInstance().activate(SceneController.GAME_SCENE);
                });
            }
        };
        Client.getInstance().addCommandListener(commandListener);

        try {
            // we may have been interrupted on the wait screen so it's better to
            // fetch it again just in case
            GameState.getInstance().index = ((GetPlayerIndexMessageResult) Client.getInstance().sendMessage(new GetPlayerIndexMessage())).index;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void switchedAway() {
        Client.getInstance().removeCommandListener(commandListener);
    }

    private void setupShip(ImageView ship, int length) {
        ship.setUserData(length);

        ship.setOnDragDetected(event -> {
            if (!ready) {
                Dragboard db = ship.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent cb = new ClipboardContent();
                cb.putString(ship.getId());
                db.setContent(cb);

                SnapshotParameters sp = new SnapshotParameters();
                sp.setFill(Color.TRANSPARENT);

                db.setDragView(ship.snapshot(sp, null));
                ship.setVisible(false);
            }

            event.consume();
        });

        ship.setOnDragDone(event -> {
            if (event.getTransferMode() == null) ship.setVisible(true);
            event.consume();
        });

        ship.setOnMouseClicked(event -> {
            if (ship.getParent() == boardGrid && event.getButton() == MouseButton.SECONDARY)
                attemptRotation(ship);
        });
    }

    private void initializeGrid() {
        for (int col = 0; col < 10; col++)
            for (int row = 0; row < 10; row++)
                boardGrid.add(new Pane(), col, row);

        boardGrid.setOnDragOver(event -> {
            if (event.getGestureSource() != boardGrid && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        boardGrid.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                String shipId = db.getString();
                ImageView ship = allShips.stream().filter(imageView -> imageView.getId().equals(shipId)).findFirst().orElse(null);

                if (ship != null) {
                    Node node = event.getPickResult().getIntersectedNode();
                    while (node != null && node != boardGrid && GridPane.getRowIndex(node) == null) {
                        node = node.getParent();
                    }

                    if (node != null && node != boardGrid) {
                        Integer col = GridPane.getColumnIndex(node);
                        Integer row = GridPane.getRowIndex(node);
                        int length = (int) ship.getUserData();
                        boolean isVertical = (ship.getRotate() != 0);

                        if (isValidPosition(ship, col, row, length, isVertical)) {
                            boolean newShip = shipsContainer.getChildren().remove(ship);
                            boardGrid.getChildren().remove(ship);

                            if (newShip) {
                                letsgo.setDisable(!shipsContainer.getChildren().isEmpty());
                                int total = 10 - shipsContainer.getChildren().size();
                                Client.getInstance().sendMessageWithoutResponse(new AssemblyPlacedShipMessage(total));
                            }

                            if (isVertical) boardGrid.add(ship, col, row, 1, length);
                            else boardGrid.add(ship, col, row, length, 1);

                            ship.setVisible(true);
                            success = true;
                        }
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void attemptRotation(ImageView ship) {
        int length = (int) ship.getUserData();
        Integer col = GridPane.getColumnIndex(ship);
        Integer row = GridPane.getRowIndex(ship);
        if (col == null || row == null) return;

        double cellWidth = boardGrid.getWidth() / 10;
        boolean isCurrentlyHorizontal = (ship.getRotate() == 0);

        if (isCurrentlyHorizontal) {
            if (isValidPosition(ship, col, row, length, true)) {
                ship.setRotate(90);
                ship.setTranslateX(-((length - 1) * cellWidth / 2));
                GridPane.setColumnSpan(ship, 1);
                GridPane.setRowSpan(ship, length);
            }
        } else {
            if (isValidPosition(ship, col, row, length, false)) {
                ship.setRotate(0);
                ship.setTranslateX(0);
                ship.setTranslateY(0);
                GridPane.setColumnSpan(ship, length);
                GridPane.setRowSpan(ship, 1);
            }
        }
    }

    private boolean isValidPosition(ImageView shipToPlace, int targetCol, int targetRow, int length, boolean isVertical) {
        if (targetCol < 0 || targetRow < 0) return false;
        if (isVertical && targetRow + length > 10) return false;
        else if (!isVertical && targetCol + length > 10) return false;

        int targetEndCol = isVertical ? targetCol : targetCol + length - 1;
        int targetEndRow = isVertical ? targetRow + length - 1 : targetRow;

        for (ImageView otherShip : allShips) {
            if (otherShip == shipToPlace) continue;
            if (otherShip.getParent() != boardGrid) continue;

            Integer otherCol = GridPane.getColumnIndex(otherShip);
            Integer otherRow = GridPane.getRowIndex(otherShip);
            if (otherCol == null || otherRow == null) continue;

            int otherLength = (int) otherShip.getUserData();
            boolean otherVertical = (otherShip.getRotate() != 0);

            int otherEndCol = otherVertical ? otherCol : otherCol + otherLength - 1;
            int otherEndRow = otherVertical ? otherRow + otherLength - 1 : otherRow;

            int dangerStartCol = otherCol - 1;
            int dangerEndCol = otherEndCol + 1;
            int dangerStartRow = otherRow - 1;
            int dangerEndRow = otherEndRow + 1;

            boolean isTouchingOrOverlapping = (
                    targetCol <= dangerEndCol &&
                            targetEndCol >= dangerStartCol &&
                            targetRow <= dangerEndRow &&
                            targetEndRow >= dangerStartRow
            );

            if (isTouchingOrOverlapping) return false;
        }

        return true;
    }
}
