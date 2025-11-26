package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class InterruptMessage extends Message {
    public InterruptMessage() {
        super(MessageCode.INTERRUPT);
    }
}
