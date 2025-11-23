package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.MessageResult;
import by.bsu.waterships.shared.types.PlayerIndex;

public class GetPlayerIndexMessageResult extends MessageResult {
    public PlayerIndex index;

    public GetPlayerIndexMessageResult(PlayerIndex index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return index.name();
    }
}
