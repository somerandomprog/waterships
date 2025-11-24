package by.bsu.waterships.shared.messages.game;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class GameReadyMessage extends Message {
    public GameReadyMessage() {
        super(MessageCode.GAME_READY);
    }
}
