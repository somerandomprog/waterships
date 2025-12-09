package by.bsu.waterships.shared.protocol;

import by.bsu.waterships.shared.types.PlayerIndex;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "GameTurnMessage")
@XmlType(propOrder = {"player"})
public class GameTurnMessage extends ActionMessage {
    @XmlAttribute(required = true)
    public PlayerIndex player;

    public GameTurnMessage() {
        super("game_finish");
    }

    public GameTurnMessage(PlayerIndex player) {
        super("game_finish");
        this.player = player;
    }
}
