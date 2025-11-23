package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class EndIntroductionMessage extends Message {
    public EndIntroductionMessage() {
        super(MessageCode.END_INTRODUCTION);
    }
}
