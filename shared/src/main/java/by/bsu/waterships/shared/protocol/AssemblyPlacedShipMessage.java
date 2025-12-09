package by.bsu.waterships.shared.protocol;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "AssemblyPlacedShipMessage")
@XmlType(propOrder = {"total"})
public class AssemblyPlacedShipMessage extends ActionMessage {
    @XmlElement(required = true)
    public int total;

    public AssemblyPlacedShipMessage() {
        super("assembly_placed_ship");
    }

    public AssemblyPlacedShipMessage(int total) {
        super("assembly_placed_ship");
        this.total = total;
    }
}
