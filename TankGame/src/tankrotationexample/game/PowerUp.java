package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PowerUp extends GameObject {
    boolean isHealthBoost;
    boolean isSpeedBoost;
    boolean isActive = true;
    static private BufferedImage healthImg;
    static private BufferedImage speedImg;

    public PowerUp(int x, int y, boolean isHealth, boolean isSpeed){
        this.x = x;
        this.y = y;
        this.hitbox = new Rectangle(x, y, 40, 40);
        this.isHealthBoost = isHealth;
        this.isSpeedBoost = isSpeed;
    }
    static void setHealthImg(BufferedImage img){
        PowerUp.healthImg = img;
    }
    public static void setSpeedImg(BufferedImage speed_img) {
        PowerUp.speedImg = speed_img;
    }
    public void update() {

    }
    public void drawImage(Graphics g) {
        if(this.isHealthBoost){
            g.drawImage(healthImg,(int)x, (int)y, 40,40 , null);
        }
        if(this.isSpeedBoost){
            g.drawImage(speedImg, (int)x, (int)y, 40,40 , null);
        }
    }
    public void collision() {
        this.isActive = false;
    }
}