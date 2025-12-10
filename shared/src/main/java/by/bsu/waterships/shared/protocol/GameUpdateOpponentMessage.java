package by.bsu.waterships.shared.protocol;

import by.bsu.waterships.shared.protocol.results.ActionResultMessage;
import by.bsu.waterships.shared.types.Board;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "GameUpdateOpponentMessage")
@XmlType(propOrder = {"result"})
public class GameUpdateOpponentMessage extends ActionMessage {
    @XmlElement(name = "result", required = true)
    public Board.AttackResult result;

    public GameUpdateOpponentMessage() {
        super("game_update_opponent");
    }

    public GameUpdateOpponentMessage(Board.AttackResult result) {
        super("game_update_opponent");
        this.result = result;
    }
}
