package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class GetPlayerIndexMessage extends Message {
    public GetPlayerIndexMessage() {
        super(MessageCode.GET_PLAYER_INDEX);
    }
}
