package tankrotationexample.game;

import tankrotationexample.GameConstants;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class Bullet extends GameObject {
    private float vx;
    private float vy;
    private float charge = 1f;
    private float R = 2;
    private int iterations  = 0;
    private boolean explosion  = true;
    private boolean invisible  = false;
    boolean checkExplode  = false;
    private String whoseBullet;
    private static BufferedImage img;
    private static BufferedImage smallExplosionImg;
    private static BufferedImage bigExplosionImg;

    Bullet(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.vx = (float) Math.round(3 * Math.cos(Math.toRadians(angle)));
        this.vy = (float) Math.round(3 * Math.sin(Math.toRadians(angle)));
        this.angle = angle;
        this.hitbox = new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }
    String getOwner() {
        return this.whoseBullet;
    }
    void setOwner(String owner) {
        this.whoseBullet = owner;
    }
    boolean getInvisible() {
        return this.invisible ;
    }
    void setInvisible(boolean val) {
        this.invisible  = val;
    }
    public void collision() {
        checkExplode = true;
    }
    static void setImage(BufferedImage image) { // used to set the static bullet image
        img = image;
    }
    static void setSmallExplosionImage(BufferedImage exp) { // used to set the explosion image
        smallExplosionImg = exp;
    }
    static void setBigExplosionImg(BufferedImage e){
        bigExplosionImg = e;
    }
    void setExplosion(boolean val){ //if val is true, we use the small explosion image, if val is false, we use the big explosion image
        this.explosion  = val;
    }
    public void increaseCharge() {
        this.charge = this.charge + 0.05f;
    }
    public void update() {
        if (!checkExplode) {
            vx = Math.round(R * Math.cos(Math.toRadians(angle)));
            vy = Math.round(R * Math.sin(Math.toRadians(angle)));
            x += vx;
            y += vy;
            checkBorder();
        } else {
            iterations++;
        }
        this.hitbox.setLocation((int) x, (int) y);
    }
    private void checkBorder() {
        if (x < 30) {
            x = 30;
            this.invisible  = true;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - 88) {
            x = GameConstants.GAME_WORLD_WIDTH - 88;
            this.invisible  = true;
        }
        if (y < 40) {
            y = 40;
            this.invisible  = true;
        }
        if (y >= GameConstants.GAME_WORLD_WIDTH - 80) {
            y = GameConstants.GAME_WORLD_WIDTH - 80;
            this.invisible  = true;
        }
    }
    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }
    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle),this.img.getWidth()/2.0, this.img.getHeight()/2.0);
        rotation.scale(this.charge, this.charge);
        Graphics2D g2d = (Graphics2D) g;
        if (checkExplode && explosion) {
            g2d.drawImage(smallExplosionImg, rotation, null);
            if (iterations >= 5) {
                this.invisible  = true;
            }
        }else if(checkExplode && !explosion){
            g2d.drawImage(bigExplosionImg, rotation, null);
            if (iterations >= 5) {
                this.invisible  = true;
            }
        } else {
            g2d.drawImage(img, rotation, null);
        }
    }
    public void setHeading(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }
}