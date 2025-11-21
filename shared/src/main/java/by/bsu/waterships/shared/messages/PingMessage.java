package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class PingMessage extends Message {
    public PingMessage() {
        super(MessageCode.PING);
    }
}
