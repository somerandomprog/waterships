package by.bsu.waterships.shared.types;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PlayerIndex")
@XmlEnum
public enum PlayerIndex {
    PLAYER_1,
    PLAYER_2
}
