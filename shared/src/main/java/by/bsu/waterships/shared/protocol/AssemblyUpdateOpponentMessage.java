package by.bsu.waterships.shared.protocol;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "AssemblyUpdateOpponentMessage")
@XmlType(propOrder = {"total"})
public class AssemblyUpdateOpponentMessage extends ActionMessage {
    @XmlElement(required = true)
    public int total;

    public AssemblyUpdateOpponentMessage() {
        super("assembly_update_opponent");
    }

    public AssemblyUpdateOpponentMessage(int total) {
        super("assembly_update_opponent");
        this.total = total;
    }
}
