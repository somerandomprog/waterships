package by.bsu.waterships.shared.types;

import java.io.Serial;
import java.io.Serializable;

public class MessageResult extends Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String error;

    public MessageResult() {
        this(null);
    }

    public MessageResult(String error) {
        super(MessageCode.RESULT);
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
