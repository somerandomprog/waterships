package by.bsu.waterships.shared.protocol;

import by.bsu.waterships.shared.types.PlayerIndex;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "GameFinishMessage")
@XmlType(propOrder = {"winner"})
public class GameFinishMessage extends ActionMessage {
    @XmlAttribute(required = true)
    public PlayerIndex winner;

    public GameFinishMessage() {
        super("game_finish");
    }

    public GameFinishMessage(PlayerIndex winner) {
        super("game_finish");
        this.winner = winner;
    }
}
