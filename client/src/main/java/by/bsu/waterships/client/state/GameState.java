package by.bsu.waterships.client.state;

import by.bsu.waterships.shared.types.PlayerIndex;
import javafx.scene.image.Image;

public class GameState {
    private static GameState instance;

    public PlayerIndex index;
    public PlayerIndex winnerIndex;

    public Image meImage;
    public Image opponentImage;
    public String meName;
    public String opponentName;

    public Image shipsSnapshot;

    public void reset() {
        meName = null;
        opponentName = null;
        meImage = null;
        opponentImage = null;
        shipsSnapshot = null;
        index = null;
        winnerIndex = null;
    }

    public static GameState getInstance() {
        if (instance == null) instance = new GameState();
        return instance;
    }
}
