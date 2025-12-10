package by.bsu.waterships.shared.protocol;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "ActionMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionMessage {
    @XmlAttribute(required = true)
    protected String action;

    @XmlAttribute
    protected String correlationId;

    public ActionMessage() {
    }

    public ActionMessage(String action) {
        this.action = action;
        this.correlationId = null;
    }

    public ActionMessage(String action, String correlationId) {
        this.action = action;
        this.correlationId = correlationId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public String toString() {
        return "ActionMessage{" +
                "action='" + action + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}