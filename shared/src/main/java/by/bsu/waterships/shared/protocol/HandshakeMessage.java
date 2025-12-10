package by.bsu.waterships.shared.protocol;

import by.bsu.waterships.shared.types.PlayerIndex;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "HandshakeMessage")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"index"})
public class HandshakeMessage extends ActionMessage {
    @XmlAttribute(required = true)
    private PlayerIndex index;

    public HandshakeMessage() {
        super("handshake");
    }

    public HandshakeMessage(PlayerIndex index) {
        super("handshake");
        this.index = index;
    }

    public PlayerIndex getIndex() {
        return index;
    }

    public void setIndex(PlayerIndex index) {
        this.index = index;
    }
}
