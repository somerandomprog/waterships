package by.bsu.waterships.shared.messages.game;

import by.bsu.waterships.shared.types.Board;
import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.Point;

public class GameUpdateOpponentMessage extends Message {
    public Board.AttackResult result;

    public GameUpdateOpponentMessage(Board.AttackResult result) {
        super(MessageCode.GAME_UPDATE_OPPONENT);
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString();
    }
}
