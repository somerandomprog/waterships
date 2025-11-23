package by.bsu.waterships.shared.types;

import java.io.Serial;
import java.io.Serializable;

public class PlayerInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public byte[] image;
    public String name;

    public PlayerInfo(byte[] image, String name) {
        this.image = image;
        this.name = name;
    }
}
