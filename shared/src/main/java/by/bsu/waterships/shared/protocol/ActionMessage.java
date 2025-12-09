package by.bsu.waterships.shared.protocol;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ActionMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionMessage {
    @XmlAttribute(required = true)
    private String action;

    @XmlAttribute
    private String correlationId;

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
}