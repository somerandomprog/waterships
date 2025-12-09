package by.bsu.waterships.shared.protocol;

import by.bsu.waterships.shared.types.Board;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "AssemblyReadyMessage")
@XmlType(propOrder = {"board"})
public class AssemblyReadyMessage extends ActionMessage {
    @XmlElement(name = "Board", required = true)
    public Board board;

    public AssemblyReadyMessage() {
        super("assembly_ready");
    }

    public AssemblyReadyMessage(Board board) {
        super("assembly_ready");
        this.board = board;
    }
}
