package tankrotationexample.game;

import java.awt.*;
//import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Wall extends GameObject {
    private boolean isBackground;
    private static BufferedImage backgroundImg;
    private static BufferedImage unbreakableWallImg;
    public Wall() {

    }
    public Wall(float x, float y, boolean is_background) {
        this.x = x;
        this.y = y;
        this.isBackground = is_background;
        this.hitbox = new Rectangle((int) x, (int) y, 32, 32);

    }
    public static void setUnbreakableWallImg(BufferedImage image) {
        unbreakableWallImg = image;
    }
    public static void setBackgroundImg(BufferedImage image) {
        backgroundImg = image;
    }
    public void update() {

    }
    public void collision() {

    }
    public void drawImage(Graphics g2d) {
        if (this.isBackground) {
            g2d.drawImage(backgroundImg,(int)x, (int)y, null);
        } else {
            g2d.drawImage(unbreakableWallImg, (int)x, (int)y, null);
        }
    }
}