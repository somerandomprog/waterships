package by.bsu.waterships.shared.protocol.results;

import by.bsu.waterships.shared.protocol.ActionMessage;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "ActionResultMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionResultMessage extends ActionMessage {
    public ActionResultMessage() {
    }

    public ActionResultMessage(String action, String correlationId) {
        this.action = action;
        this.correlationId = correlationId;
    }

    @Override
    public String toString() {
        return "ActionResultMessage{" +
                "action='" + action + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}
