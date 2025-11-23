package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.MessageResult;
import by.bsu.waterships.shared.types.PlayerInfo;

public class SubmitIntroductionProgressMessageResult extends MessageResult {
    public PlayerInfo info;

    public SubmitIntroductionProgressMessageResult(PlayerInfo info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return String.format("name = %s, image = %d bytes", info.name, info.image.length);
    }
}
