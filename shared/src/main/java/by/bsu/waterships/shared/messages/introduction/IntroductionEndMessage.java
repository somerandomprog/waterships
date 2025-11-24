package by.bsu.waterships.shared.messages.introduction;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class IntroductionEndMessage extends Message {
    public IntroductionEndMessage() {
        super(MessageCode.INTRODUCTION_END);
    }
}
