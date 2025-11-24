package by.bsu.waterships.shared.messages.game;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class GameBeginMessage extends Message {
    public GameBeginMessage() {
        super(MessageCode.GAME_BEGIN);
    }
}
