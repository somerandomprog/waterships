package by.bsu.waterships.shared.protocol;

import by.bsu.waterships.shared.protocol.results.ActionResultMessage;
import by.bsu.waterships.shared.types.Board;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "GameUpdateOpponentMessage")
@XmlType(propOrder = {"result"})
public class GameUpdateOpponentMessage extends ActionMessage {
    @XmlElement(name = "result", required = true)
    public Board.AttackResult result;

    public GameUpdateOpponentMessage() {
        super("game_attack_result");
    }

    public GameUpdateOpponentMessage(Board.AttackResult result) {
        super("game_attack_result");
        this.result = result;
    }
}
