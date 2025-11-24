package by.bsu.waterships.shared.messages.game;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.Point;

public class GameAttackMessage extends Message {
    public Point where;

    public GameAttackMessage(Point where) {
        super(MessageCode.GAME_ATTACK);
        this.where = where;
    }

    @Override
    public String toString() {
        return where.toString();
    }
}
