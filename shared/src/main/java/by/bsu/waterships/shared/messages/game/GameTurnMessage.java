package by.bsu.waterships.shared.messages.game;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.PlayerIndex;

public class GameTurnMessage extends Message {
    public PlayerIndex player;

    public GameTurnMessage(PlayerIndex player) {
        super(MessageCode.GAME_TURN);
        this.player = player;
    }

    @Override
    public String toString() {
        return player.name();
    }
}
