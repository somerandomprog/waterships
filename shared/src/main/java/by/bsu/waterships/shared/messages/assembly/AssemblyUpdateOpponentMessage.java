package by.bsu.waterships.shared.messages.assembly;

import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class AssemblyUpdateOpponentMessage extends Message {
    public int total;

    public AssemblyUpdateOpponentMessage(int total) {
        super(MessageCode.ASSEMBLY_UPDATE_OPPONENT);
        this.total = total;
    }

    @Override
    public String toString() {
        return "total: " + total;
    }
}
