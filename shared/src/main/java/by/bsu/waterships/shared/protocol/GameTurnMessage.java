package by.bsu.waterships.shared.protocol;

import by.bsu.waterships.shared.types.PlayerIndex;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "GameTurnMessage")
@XmlType(propOrder = {"player"})
public class GameTurnMessage extends ActionMessage {
    @XmlAttribute(required = true)
    public PlayerIndex player;

    public GameTurnMessage() {
        super("game_turn");
    }

    public GameTurnMessage(PlayerIndex player) {
        super("game_turn");
        this.player = player;
    }
}
