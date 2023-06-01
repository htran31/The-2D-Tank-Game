package tankrotationexample.game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BreakableWall extends Wall {
    private int health = 100;
    private static BufferedImage img;
    private boolean isRemoved = false;
    private boolean isBreakable = false;

    BreakableWall(float x, float y,  BufferedImage img) {
        this.x = x;
        this.y = y;
        this.img = img;
        this.hitbox = new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }
    int getHealth() {
        return this.health;
    }
    boolean checkIfRemoved() {
        return isRemoved;
    }
    private void addHealth(int value) {
        if (health + value > 100) {
            health = 100;
        } else {
            health += value;
        }
    }
    private void removeHealth(int value) {
        if (health - value < 0) {
            health = 0;
            isRemoved = true;
        } else {
            health -= value;
        }
    }
    static void setImage(BufferedImage image) {
        BreakableWall.img = image;
    }
    public void update() {

    }
    public void collision() {
        this.removeHealth(50);
    }
    public void drawImage(Graphics2D g2d) {
        if (!isRemoved) {
            g2d.drawImage(img, (int)x, (int)y, null);
        }
    }
}