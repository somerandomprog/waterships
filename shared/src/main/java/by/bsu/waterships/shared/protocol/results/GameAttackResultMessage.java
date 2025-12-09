package by.bsu.waterships.shared.protocol.results;

import by.bsu.waterships.shared.types.Board;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "GameAttackResultMessage")
@XmlType(propOrder = {"result"})
public class GameAttackResultMessage extends ActionResultMessage {
    @XmlElement(name = "result", required = true)
    public Board.AttackResult result;

    public GameAttackResultMessage() {
        super("game_attack_result", null);
    }

    public GameAttackResultMessage(String correlationId, Board.AttackResult result) {
        super("game_attack_result", correlationId);
        this.result = result;
    }
}
