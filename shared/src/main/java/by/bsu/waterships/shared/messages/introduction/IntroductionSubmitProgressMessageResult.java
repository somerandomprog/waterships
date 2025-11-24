package by.bsu.waterships.shared.messages.introduction;

import by.bsu.waterships.shared.types.MessageResult;
import by.bsu.waterships.shared.types.PlayerInfo;

public class IntroductionSubmitProgressMessageResult extends MessageResult {
    public PlayerInfo info;

    public IntroductionSubmitProgressMessageResult(PlayerInfo info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return String.format("name = %s, image = %d bytes", info.name(), info.image().length);
    }
}
