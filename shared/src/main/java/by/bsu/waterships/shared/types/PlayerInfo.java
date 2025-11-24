package by.bsu.waterships.shared.types;

import java.io.Serializable;

public record PlayerInfo(byte[] image, String name) implements Serializable {
}
