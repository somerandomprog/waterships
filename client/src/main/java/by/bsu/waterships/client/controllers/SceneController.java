package by.bsu.waterships.client.controllers;

import by.bsu.waterships.client.WatershipsApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class SceneController {
    public static final String MENU_SCENE = "menu";
    public static final String ABOUT_SCENE = "about";
    public static final String CONNECT_TO_SERVER_SCENE = "connect_to_server";
    public static final String WAIT_SCENE = "wait";
    public static final String INTRODUCE_SCENE = "introduce";
    public static final String ASSEMBLE_BOARD_SCENE = "assemble_board";
    public static final String PLAY_SCENE = "play";
    public static final String END_SCENE = "end";

    private static SceneController instance;

    private String current;
    private final HashMap<String, Parent> screens = new HashMap<>();
    private final HashMap<String, Object> controllers = new HashMap<>();
    private final Stage main;

    private SceneController(Stage main) {
        this.main = main;
    }

    public void add(String id, String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(WatershipsApplication.class.getResource(path)));
        screens.put(id, loader.load());
        controllers.put(id, loader.getController());
    }

    public void activate(String id) {
        tryCallControllerMethod(current, "switchedAway");
        main.getScene().setRoot(screens.get(id));
        current = id;
        tryCallControllerMethod(id, "switched");
    }

    public static SceneController getInstance(Stage main) {
        if (instance == null) instance = new SceneController(main);
        return instance;
    }

    public static SceneController getInstance() {
        assert instance != null && instance.main != null;
        return instance;
    }

    private void tryCallControllerMethod(String id, String method) {
        try {
            Class<?> controllerClass = controllers.get(id).getClass();
            controllerClass.getMethod(method).invoke(controllers.get(id));
        } catch (Exception ignored) {}
    }
}
