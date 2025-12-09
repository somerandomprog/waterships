package by.bsu.waterships.shared.protocol.results;

import by.bsu.waterships.shared.types.PlayerInfo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "IntroductionSubmitProgressResultMessage")
@XmlType(propOrder = {"info"})
public class IntroductionSubmitProgressResultMessage extends ActionResultMessage {
    @XmlElement(name = "PlayerInfo", required = true)
    public PlayerInfo info;

    public IntroductionSubmitProgressResultMessage() {
        super("introductionsubmit_progress_result", null);
    }

    public IntroductionSubmitProgressResultMessage(String correlationId, PlayerInfo info) {
        super("introduction_submit_progress_result", correlationId);
        this.info = info;
    }

    public PlayerInfo getInfo() {
        return info;
    }

    public void setInfo(PlayerInfo info) {
        this.info = info;
    }
}