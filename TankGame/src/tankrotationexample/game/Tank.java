package tankrotationexample.game;

import tankrotationexample.GameConstants;
import tankrotationexample.Resources;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Tank extends GameObject {
    private float vx;
    private float vy;
    private float screen_x;
    private float screen_y;
    private float angle;
    private float R = 5;
    private float ROTATIONSPEED = 3.0f;
    private int health = 100;
    private long timeSinceLastShot  = 0L;
    private long speedBoost = 0;
    private long cooldown = 1000;
    private boolean isSpeedBoost;
    private boolean move = true;
    private boolean UpPressed;
    private boolean DownPressed;
    private boolean RightPressed;
    private boolean LeftPressed;
    private boolean shootPressed;
    private BufferedImage img;
    private GameWorld gw;
    private String id;
    Bullet currentChargeBullet = null;
    List<Bullet> ammo = new ArrayList<>();




    Tank(float x, float y,  BufferedImage img) {
        this.x = x;
        this.y = y;
        this.vx = 0;
        this.vy = 0;
        this.img = img;
        this.angle = 0;
        this.hitbox = new Rectangle((int) x, (int) y, img.getWidth(), img.getHeight());
    }
    public float getScreen_x() {
        return screen_x;
    }
    public float getScreen_y() {
        return screen_y;
    }
    public float getX() {
        return this.x;
    }
    public float getY() {
        return this.y;
    }
    void setX(float x){ this.x = x; }
    void setY(float y) { this. y = y;}
    void setGW(GameWorld gw_to_set) { //this is needed for spawning bullets
        this.gw = gw_to_set;
    }
    void setHealth(int health_to_set) {
        this.health = health_to_set;
    }
    void setID(String tag) {
        this.id = tag;
    }
    String getID() {
        return id;
    }




    void toggleUpPressed() {
        this.UpPressed = true;
    }

    void toggleDownPressed() {
        this.DownPressed = true;
    }

    void toggleRightPressed() {
        this.RightPressed = true;
    }

    void toggleLeftPressed() {
        this.LeftPressed = true;
    }

    void unToggleUpPressed() {
        this.UpPressed = false;
    }

    void unToggleDownPressed() {
        this.DownPressed = false;
    }

    void unToggleRightPressed() {
        this.RightPressed = false;
    }

    void unToggleLeftPressed() {
        this.LeftPressed = false;
    }





    public void update() {
        this.hitbox.setLocation((int) x, (int) y);
        if (this.UpPressed) {
            this.moveForwards();
        }
        if (this.DownPressed) {
            this.moveBackwards();
        }
        if (this.LeftPressed) {
            this.rotateLeft();
        }
        if (this.RightPressed) {
            this.rotateRight();
        }
        if (this.shootPressed && ((this.timeSinceLastShot + this.cooldown) < System.currentTimeMillis())) {
            if (this.currentChargeBullet == null) {
                this.currentChargeBullet = new Bullet(x, y, angle);
            }else{
                this.currentChargeBullet.increaseCharge();
                this.currentChargeBullet.setHeading(x, y, angle);
            }
            Sound b = Resources.getSound("bullet");
            b.setVolume(2.3f);
            b.play();
            //Animation a = new Animation(Resources.getAnimation("bullet"), safeShootX(), safeShootY());
            //gw.activeAnimations.add(a);
            this.SpawnBullet( x , y , vx, vy, angle, gw);
            timeSinceLastShot  = System.currentTimeMillis();
        }else {
            if (this.currentChargeBullet != null) {
                this.ammo.add(this.currentChargeBullet);
                this.timeSinceLastShot  = System.currentTimeMillis();
                this.currentChargeBullet = null;
            }
        }
        this.move = true;
        this.ammo.forEach(bullet -> bullet.update());
        this.hitbox.setLocation((int) x, (int) y);
    }






    private int safeShootX(){
        double cx = 31 * Math.cos(Math.toRadians(this.angle));
        return (int) (x + this.img.getWidth()/2f + cx - 4f);
    }
    private int safeShootY(){
        double cy = 31 * Math.sin(Math.toRadians(this.angle));
        return (int) (y + this.img.getWidth()/2f + cy - 4f);
    }
    private void rotateLeft() {
        this.angle -= this.ROTATIONSPEED;
    }
    private void rotateRight() {
        this.angle += this.ROTATIONSPEED;
    }
    private void moveBackwards() {
        vx = (float) Math.round(R * Math.cos(Math.toRadians(angle))) * -1;
        vy = (float) Math.round(R * Math.sin(Math.toRadians(angle))) * -1;
        if (this.isSpeedBoost && (System.currentTimeMillis() - speedBoost < cooldown)) {
            vx = (float) Math.round(4 * R * Math.cos(Math.toRadians(angle))) * -1;
            vy = (float) Math.round(4 * R * Math.sin(Math.toRadians(angle))) * -1;
        } else if (this.checkIfSpeedBoost() && (System.currentTimeMillis() - speedBoost < cooldown)) {
            speedBoost = 0;
            this.isSpeedBoost = false;
        }
        if (move) {
            x += vx;
            y += vy;
        }
        checkBorder();
        centerScreen();
    }
    private void moveForwards() {
        vx = (float) Math.round(R * Math.cos(Math.toRadians(angle)));
        vy = (float) Math.round(R * Math.sin(Math.toRadians(angle)));
        if (System.currentTimeMillis() - speedBoost < cooldown && isSpeedBoost) {
            vx = (float) Math.round(4 * R * Math.cos(Math.toRadians(angle)));
            vy = (float) Math.round(4 * R * Math.sin(Math.toRadians(angle)));
        } else if (this.checkIfSpeedBoost() && (System.currentTimeMillis() - speedBoost < cooldown)) {
            speedBoost = 0;
            this.isSpeedBoost = false;
        }
        if (move) {
            x += vx;
            y += vy;
        }
        checkBorder();
        centerScreen();
    }
    private void centerScreen() {
    }
    private void checkBorder() {
        if (x < 30) {
            x = 30;
        }
        if (x >= GameConstants.GAME_WORLD_WIDTH - 88) {
            x = GameConstants.GAME_WORLD_WIDTH - 88;
        }
        if (y < 40) {
            y = 40;
        }
        if (y >= GameConstants.GAME_WORLD_HEIGHT - 80) {
            y =GameConstants.GAME_WORLD_HEIGHT - 80;
        }
    }






    public void drawImage(Graphics g) {
        AffineTransform rotation = AffineTransform.getTranslateInstance(x, y);
        rotation.rotate(Math.toRadians(angle), this.img.getWidth()/2.0, this.img.getHeight()/2.0);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(this.img, rotation, null);
        //this.ammo.forEach(b -> b.drawImage(g));
        if (this.health != 0) {
            g2d.drawImage(this.img, rotation, null);
        }
        if (this.currentChargeBullet != null) {
            this.currentChargeBullet.drawImage(g);
        }
        g.setColor(Color.GREEN);
        g.drawRect((int) x, (int) y-20, 100, 15);
        long currentWidth = 100 - ((this.timeSinceLastShot  + this.cooldown) - System.currentTimeMillis())/40;
        if (currentWidth > 100) {
            currentWidth = 100;
        }
        g.fillRect((int) x, (int) y-20, (int)currentWidth, 15);

    }

    public void collision() { //this is only called if the tank is shot by a bullet(other than its own)
        this.removeHealth(10);
    }
    public void unToggleShootPressed() {
        this.shootPressed = false;
    }
    public void toggleShootPressed() {
        this.shootPressed = true;
    }
    @Override
    public String toString() {
        return "x=" + x + ", y=" + y + ", angle=" + angle;
    }
    public void setAngle(float angle) {
        this.angle = angle;
    }
    private void SpawnBullet(float x, float y, float vx, float vy, float angle, GameWorld gw) {
        Bullet b = new Bullet(x, y, angle);
        b.setOwner(id);
        gw.addGameObject(b);
    }
    void addHealth(float val) {
        if (health + val > 100) {
            health = 100;
        } else {
            health += val;
        }
    }
    private void removeHealth(float val) {
        if (health - val < 0) {
            health = 0;
        } else {
            health -= val;
        }
    }
    int getHealth() {
        return this.health;
    }
    Rectangle getOffsetBounds() {
        return new Rectangle((int)(x + vx), (int) (y + vy), 50, 50);
    }
    long getSpeedBoost() {
        return speedBoost;
    }
    void setSpeedBoost(long speedBoost) {
        this.speedBoost = speedBoost;
    }
    private boolean checkIfSpeedBoost() {
        return isSpeedBoost;
    }
    void setIfSpeedBoost(boolean speed_isboosted) {
        this.isSpeedBoost = speed_isboosted;
    }
    void setMove(boolean m){
        this.move = m;
    }
    boolean geMove(){
        return this.move;
    }
}