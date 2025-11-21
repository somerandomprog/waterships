package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class DisconnectMessage extends Message {
    public DisconnectMessage() {
        super(MessageCode.DISCONNECT);
    }
}
