package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.PlayerInfo;

public class UpdateOpponentIntroductionMessage extends Message {
    public final PlayerInfo info;

    public UpdateOpponentIntroductionMessage(PlayerInfo info) {
        super(MessageCode.UPDATE_OPPONENT_INTRODUCTION);
        this.info = info;
    }

    @Override
    public String toString() {
        return String.format("name = %s, image = %d bytes", info.name, info.image.length);
    }
}
