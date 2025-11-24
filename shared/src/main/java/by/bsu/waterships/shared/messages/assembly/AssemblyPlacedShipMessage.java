package by.bsu.waterships.shared.messages.assembly;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class AssemblyPlacedShipMessage extends Message {
    public int total;

    public AssemblyPlacedShipMessage(int total) {
        super(MessageCode.ASSEMBLY_PLACE_SHIP);
        this.total = total;
    }

    @Override
    public String toString() {
        return "total: " + total;
    }
}
