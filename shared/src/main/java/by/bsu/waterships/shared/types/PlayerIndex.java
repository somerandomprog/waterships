package by.bsu.waterships.shared.types;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "PlayerIndex")
@XmlEnum
public enum PlayerIndex {
    PLAYER_1,
    PLAYER_2
}
