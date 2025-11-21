package by.bsu.waterships.shared.types;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final MessageCode code;

    public Message(MessageCode code) {
        this.code = code;
    }

    public MessageCode getCode() {
        return code;
    }
}
