package tankrotationexample.game;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
    private Clip sound;
    public Sound(Clip sound) {
        this.sound = sound;
    }
    public void play(){
        this.sound.setFramePosition(0);
        this.sound.start();
    }
    public void stop(){
        if (this.sound.isRunning()) {
            this.sound.stop();
        }
    }
    public void setLooping() {
        this.sound.loop(Clip.LOOP_CONTINUOUSLY);
    }
    public void setVolume(float level) {
        FloatControl volume = (FloatControl) this.sound.getControl(FloatControl.Type.MASTER_GAIN);
        volume.setValue(10.0f * (float) Math.log10(level));
    }
}