package by.bsu.waterships.client.state;

import by.bsu.waterships.client.WatershipsApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class SceneController {
    public abstract static class WatershipsScene {
        public void switched() {
        }

        public void switchedAway() {
        }
    }

    public static final String MENU_SCENE = "menu";
    public static final String ABOUT_SCENE = "about";
    public static final String CONNECT_TO_SERVER_SCENE = "connect_to_server";
    public static final String WAIT_SCENE = "wait";
    public static final String INTRODUCE_SCENE = "introduce";
    public static final String ASSEMBLE_BOARD_SCENE = "assemble_board";
    public static final String GAME_SCENE = "game";
    public static final String END_SCENE = "end";
    public static final String INTERRUPTED_SCENE = "interrupted";

    private static SceneController instance;

    private String current;
    private final HashMap<String, Parent> screens = new HashMap<>();
    private final HashMap<String, WatershipsScene> controllers = new HashMap<>();
    private final Stage main;

    private SceneController(Stage main) {
        this.main = main;
    }

    public void add(String id, String path) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(WatershipsApplication.class.getResource(path)));
        screens.put(id, loader.load());
        if (!(loader.getController() instanceof WatershipsScene ws))
            throw new Exception("all scenes in waterships must extend WatershipsScene");
        else controllers.put(id, ws);
    }

    public void activate(String id) {
        if (current != null) controllers.get(current).switchedAway();
        main.getScene().setRoot(screens.get(id));
        current = id;
        controllers.get(id).switched();
    }

    public static SceneController getInstance(Stage main) {
        if (instance == null) instance = new SceneController(main);
        return instance;
    }

    public static SceneController getInstance() {
        assert instance != null && instance.main != null;
        return instance;
    }

    public String getCurrent() {
        return current;
    }
}
