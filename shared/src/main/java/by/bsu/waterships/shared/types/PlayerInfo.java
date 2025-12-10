package by.bsu.waterships.shared.types;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

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