package controller;

import java.net.URL;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundController extends GameController {

    public enum Sounds {
        GRASS("walking_grass.wav"),
        WATER("walking_water.wav"),
        COIN("coin.wav"),
        TURNAROUND("turnaround.wav"),
        ERROR("error.wav");

        private final String path;

        Sounds(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    private static final String DIR = "sounds\\";
    private boolean enabled;

    public SoundController(HeroSimulatorStage sim) {
        super(sim);
        this.enabled = sim.getPropLoader().getSound();
    }

    public void playSound(Sounds s) {
        if (!enabled) return;

        String file = DIR + s.getPath();
        URL url = getClass().getClassLoader().getResource(file);
        try {
            assert url != null;
            String uri = url.toURI().toString();
            Media sound = new Media(uri);
            MediaPlayer player = new MediaPlayer(sound);
            player.play();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
