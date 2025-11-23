package by.bsu.waterships.client.state;

import javafx.scene.image.Image;

public class GameState {
    private static GameState instance;

    public Image meImage;
    public Image opponentImage;
    public String meName;
    public String opponentName;

    public void reset() {
        meName = null;
        opponentName = null;
        meImage = null;
        opponentImage = null;
    }

    public static GameState getInstance() {
        if (instance == null) instance = new GameState();
        return instance;
    }
}
