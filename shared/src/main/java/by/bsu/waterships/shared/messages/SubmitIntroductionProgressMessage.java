package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class SubmitIntroductionProgressMessage extends Message {
    public SubmitIntroductionProgressMessage() {
        super(MessageCode.SUBMIT_INTRODUCTION_PROGRESS);
    }
}
