package by.bsu.waterships.shared.messages.game;

import by.bsu.waterships.shared.types.Board;
import by.bsu.waterships.shared.types.MessageResult;

public class GameAttackMessageResult extends MessageResult {
    public Board.AttackResult result;

    public GameAttackMessageResult(Board.AttackResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return result.toString();
    }
}
