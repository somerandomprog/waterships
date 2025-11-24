package by.bsu.waterships.shared.messages.introduction;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class IntroductionStartMessage extends Message {
    public int seconds;

    public IntroductionStartMessage(int seconds) {
        super(MessageCode.INTRODUCTION_START);
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return seconds + " seconds";
    }
}