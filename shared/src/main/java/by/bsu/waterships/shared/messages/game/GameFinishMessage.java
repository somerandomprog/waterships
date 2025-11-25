package by.bsu.waterships.shared.messages.game;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.PlayerIndex;

public class GameFinishMessage extends Message {
    public PlayerIndex winner;

    public GameFinishMessage(PlayerIndex winner) {
        super(MessageCode.GAME_FINISH);
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "winner: " + winner.name();
    }
}
