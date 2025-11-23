package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class StartIntroductionMessage extends Message {
    public int seconds;

    public StartIntroductionMessage(int seconds) {
        super(MessageCode.START_INTRODUCTION);
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return seconds + " seconds";
    }
}