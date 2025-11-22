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

    private static SceneController instance;

    private final HashMap<String, Parent> screens = new HashMap<>();
    private final Stage main;

    private SceneController(Stage main) {
        this.main = main;
    }

    public void add(String id, String path) throws IOException {
        screens.put(id, new FXMLLoader(Objects.requireNonNull(WatershipsApplication.class.getResource(path))).load());
    }

    public void activate(String id) {
        main.getScene().setRoot(screens.get(id));
    }

    public static SceneController getInstance(Stage main) {
        if (instance == null) instance = new SceneController(main);
        return instance;
    }

    public static SceneController getInstance() {
        assert instance != null && instance.main != null;
        return instance;
    }
}
