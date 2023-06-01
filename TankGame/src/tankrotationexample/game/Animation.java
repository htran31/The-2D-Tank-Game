package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Animation {
    private List<BufferedImage> frame;
    private float x, y;
    private long delay = 25;
    private int currentFrame;
    private long timeSinceLastFrameUpdate = 0L;
    private boolean isRunning = false;

    public Animation(List<BufferedImage> frame, float x, float y) {
        this.frame = frame;
        this.x = x - frame.get(0).getWidth()/2f;
        this.y = y - frame.get(0).getHeight()/2f;
        this.isRunning = true;
    }
    public boolean isRunning() {
        return isRunning;
    }
    public void update() {
        if (this.timeSinceLastFrameUpdate + this.delay < System.currentTimeMillis()) {
            this.currentFrame++;
            this.timeSinceLastFrameUpdate = System.currentTimeMillis();
            //this.currentFrame = (this.currentFrame + 1) % this.frame.size();
            if (this.currentFrame == this.frame.size() - 1) {
                isRunning = false;
            }
        }
    }
    public void drawImage(Graphics2D g2d){
        if(isRunning) {
            g2d.drawImage(this.frame.get(this.currentFrame), (int)x, (int)y, null);
        }
    }
}
