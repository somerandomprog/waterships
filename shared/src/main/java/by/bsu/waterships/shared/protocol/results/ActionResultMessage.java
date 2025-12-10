package by.bsu.waterships.shared.protocol.results;

import by.bsu.waterships.shared.protocol.ActionMessage;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ActionResultMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionResultMessage extends ActionMessage {
    @XmlAttribute(required = true)
    private String action;

    @XmlAttribute(required = true)
    private String correlationId;

    public ActionResultMessage() {
    }

    public ActionResultMessage(String action, String correlationId) {
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
}
