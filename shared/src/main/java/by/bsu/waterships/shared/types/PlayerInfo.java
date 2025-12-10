package by.bsu.waterships.shared.types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PlayerInfoType", propOrder = {"image", "name"})
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerInfo {

    @XmlElement(required = true)
    public byte[] image;

    @XmlElement(required = true)
    public String name;

    public PlayerInfo() {
    }

    public PlayerInfo(byte[] image, String name) {
        this.image = image;
        this.name = name;
    }
}