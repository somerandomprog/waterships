package by.bsu.waterships.shared.messages.assembly;

import by.bsu.waterships.shared.types.Board;
import by.bsu.waterships.shared.types.Message;
import by.bsu.waterships.shared.types.MessageCode;

public class AssemblyReadyMessage extends Message {
    public Board board;

    public AssemblyReadyMessage(Board board) {
        super(MessageCode.ASSEMBLY_READY);
        this.board = board;
    }

    @Override
    public String toString() {
        return "\n" + board.toString();
    }
}
