package by.bsu.waterships.client.state;

import by.bsu.waterships.client.WatershipsApplication;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

import java.util.Objects;

public class Resources {
    public static Image DEATH_CELL_TEXTURE = new Image(Objects.requireNonNull(WatershipsApplication.class.getResourceAsStream("textures/board/death.png")));
    public static Image MISSED_CELL_TEXTURE = new Image(Objects.requireNonNull(WatershipsApplication.class.getResourceAsStream("textures/board/miss.png")));

    public static class SFX {
        public static AudioClip WIN_SFX = clip("sounds/win.mp3");
        public static AudioClip SHIP_DESTROYED_SFX = clip("sounds/ship_destroyed.mp3");
        public static AudioClip SCRIBBLE_SFX = clip("sounds/scribble.mp3");
        public static AudioClip RESTART_SFX = clip("sounds/restart.mp3");
        public static AudioClip PAPER_SFX = clip("sounds/paper.mp3");
        public static AudioClip NOTIFICATION_SFX = clip("sounds/notification.wav");
        public static AudioClip MISS_SFX = clip("sounds/miss.mp3");
        public static AudioClip LOSE_SFX = clip("sounds/lose.mp3");
        public static AudioClip HORN_SFX = clip("sounds/horn.mp3");
        public static AudioClip EXPLOSION_SFX = clip("sounds/explosion.mp3");
        public static AudioClip DRAWING_SFX = clip("sounds/drawing.mp3");
        public static AudioClip SNEAKY_SFX = clip("sounds/sneaky.mp3");
        public static AudioClip START_SFX = clip("sounds/start.mp3");
        public static AudioClip INTERRUPTED_SFX = clip("sounds/interrupt.mp3");

        private static AudioClip clip(String path) {
            return new AudioClip(Objects.requireNonNull(WatershipsApplication.class.getResource(path)).toString());
        }
    }
}
