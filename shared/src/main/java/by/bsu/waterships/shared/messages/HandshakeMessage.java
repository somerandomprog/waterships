package by.bsu.waterships.shared.messages;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;
import by.bsu.waterships.shared.types.PlayerIndex;

public class HandshakeMessage extends Message {
    public PlayerIndex index;

    public HandshakeMessage(PlayerIndex index) {
        super(MessageCode.HANDSHAKE);
        this.index = index;
    }

    @Override
    public String toString() {
        return index.name();
    }
}
