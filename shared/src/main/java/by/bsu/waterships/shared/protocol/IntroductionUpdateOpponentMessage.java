package by.bsu.waterships.shared.protocol;

import by.bsu.waterships.shared.types.PlayerInfo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "IntroductionUpdateOpponentMessage")
@XmlType(propOrder = {"info"})
public class IntroductionUpdateOpponentMessage extends ActionMessage {
    @XmlElement(name = "PlayerInfo", required = true)
    private PlayerInfo info;

    public IntroductionUpdateOpponentMessage() {
        super("introduction_update_opponent");
    }

    public IntroductionUpdateOpponentMessage(PlayerInfo info) {
        super("introduction_update_opponent");
        this.info = info;
    }

    public PlayerInfo getInfo() {
        return info;
    }

    public void setInfo(PlayerInfo info) {
        this.info = info;
    }
}
