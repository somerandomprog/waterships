package by.bsu.waterships.shared.types;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String correlationId = UUID.randomUUID().toString();
    private final MessageCode code;

    public Message(MessageCode code) {
        this.code = code;
    }

    public MessageCode getCode() {
        return code;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    protected void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public MessageResult respond(MessageResult with) {
        with.setCorrelationId(correlationId);
        return with;
    }
}
