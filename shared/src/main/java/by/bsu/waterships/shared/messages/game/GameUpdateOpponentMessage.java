package by.bsu.waterships.shared.messages.game;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class GameUpdateOpponentMessage extends Message {
    public GameUpdateOpponentMessage() {
        super(MessageCode.GAME_UPDATE_OPPONENT);
    }
}
