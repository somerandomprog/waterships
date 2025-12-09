package by.bsu.waterships.shared.protocol;

import by.bsu.waterships.shared.types.Point;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "GameAttackMessage")
@XmlType(propOrder = {"where"})
public class GameAttackMessage extends ActionMessage {
    @XmlElement(name = "where", required = true)
    public Point where;

    public GameAttackMessage() {
        super("game_attack");
    }

    public GameAttackMessage(String correlationId, Point where) {
        super("game_attack", correlationId);
        this.where = where;
    }
}
