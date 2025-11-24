package by.bsu.waterships.shared.messages.introduction;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.PlayerInfo;

public class IntroductionUpdateOpponentMessage extends Message {
    public final PlayerInfo info;

    public IntroductionUpdateOpponentMessage(PlayerInfo info) {
        super(MessageCode.INTRODUCTION_UPDATE_OPPONENT);
        this.info = info;
    }

    @Override
    public String toString() {
        return String.format("name = %s, image = %d bytes", info.name(), info.image().length);
    }
}
