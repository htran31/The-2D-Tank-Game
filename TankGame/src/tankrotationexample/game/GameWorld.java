package tankrotationexample.game;

import tankrotationexample.GameConstants;
import tankrotationexample.Launcher;
import tankrotationexample.Resources;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GameWorld extends JPanel implements Runnable {
    private final int t1SpawnX = 200;
    private final int t1SpawnY = 150;
    private final int t2SpawnX = 1700;
    private final int t2SpawnY = 1300;
    private final int t1SpawnAngle = 0;
    private final int t2SpawnAngle = 180;
    private static BufferedImage winImage;
    private static BufferedImage loseImage;
    private BufferedImage world;
    private final Launcher lf;
    private long tick = 0;
    private Tank t1;
    private Tank t2;
    private boolean t1Win = false;
    private boolean t2Win = false;
    private int t1NumLives = 2;
    private int t2NumLives = 2;
    private List<Animation> activeAnimations = new ArrayList<>(15);
    private List<GameObject> gameObjects = new ArrayList<>();
    private static Sound bg, hit, fire, lose, win;

    /**
     *
     */
    public GameWorld(Launcher lf) {
        this.lf = lf;
    }
    public void run() {
        try {
            while (true) {
                for (int i = 0; i < this.gameObjects.size(); i++) {
                    if (this.gameObjects.get(i) instanceof Bullet) {
                        if (((Bullet) this.gameObjects.get(i)).getInvisible()) {
                            this.gameObjects.remove(i);
                            i--;
                        } else {
                            this.gameObjects.get(i).update();
                        }
                    }
                    if (this.gameObjects.get(i) instanceof Tank) {
                        if (((Tank) this.gameObjects.get(i)).getHealth() == 0) { //check Tank be destroyed
                            if ((((Tank) this.gameObjects.get(i)).getID()).equals("t1")) {
                                if (this.t1NumLives > 1) {
                                    this.t1NumLives--;
                                    //respawning
                                    ((Tank) this.gameObjects.get(i)).setHealth(50);
                                    ((Tank) this.gameObjects.get(i)).setX(this.t1SpawnX);
                                    ((Tank) this.gameObjects.get(i)).setY(this.t1SpawnX);
                                    ((Tank) this.gameObjects.get(i)).setAngle(this.t1SpawnAngle);
                                } else {
                                    this.t1NumLives = 0;
                                    this.t2Win = true;
                                    lose = Resources.getSound("lose");
                                    lose.setVolume(4.0f);
                                    lose.play();
                                    break;
                                }
                            }
                            if ((((Tank) this.gameObjects.get(i)).getID()).equals("t2")) {
                                if (this.t2NumLives > 1) {
                                    this.t2NumLives--;
                                    hit = Resources.getSound("hit");
                                    hit.setVolume(4.0f);
                                    hit.play();
                                    ((Tank) this.gameObjects.get(i)).setHealth(50);
                                    ((Tank) this.gameObjects.get(i)).setX(this.t2SpawnX);
                                    ((Tank) this.gameObjects.get(i)).setY(this.t2SpawnY);
                                    ((Tank) this.gameObjects.get(i)).setAngle(this.t2SpawnAngle);
                                } else {
                                    this.t2NumLives = 0;
                                    this.t1Win = true;
                                    win = Resources.getSound("win");
                                    win.setVolume(4.0f);
                                    win.play();
                                    break;
                                }
                            }
                        }
                    }
                    if (((this.gameObjects.get(i) instanceof BreakableWall) &&
                            ((BreakableWall) this.gameObjects.get(i)).getHealth() == 0)) {
                        this.gameObjects.remove(i);
                        fire = Resources.getSound("fire");
                        fire.setVolume(4.0f);
                        fire.play();
                    }
                }
                this.t1.update(); // update tank
                this.t2.update();
                this.checkCollision();
                this.repaint();   // redraw game
                /*
                 * Sleep for 1000/144 ms (~6.9ms). This is done to have our
                 * loop run at a fixed rate per/sec.
                 */
                Thread.sleep(1000 / 144);
            }
        } catch (InterruptedException ignored) {
            System.out.println(ignored);
        }
    }

    private void checkCollision() {
        for (int i = 0; i < gameObjects.size(); i++) {
            for (int j = i; j < gameObjects.size(); j++) {
                GameObject obj_at_i = gameObjects.get(i);
                GameObject obj_at_j = gameObjects.get(j);
                if (i != j) {
                    if (obj_at_i instanceof Bullet && obj_at_j instanceof Tank &&
                            !(((Bullet) obj_at_i).getOwner().equals(((Tank) obj_at_j).getID())) &&
                            !((Bullet) obj_at_i).checkExplode ) {
                        if (obj_at_i.hitbox.intersects(obj_at_j.hitbox)) {
                            obj_at_i.collision();
                            ((Bullet) obj_at_i).setExplosion(false);
                            obj_at_j.collision();
                        }
                    }
                    if (obj_at_i instanceof Tank && obj_at_j instanceof Bullet
                            && !((Bullet) obj_at_j).getOwner().equals(((Tank) obj_at_i).getID())
                            && !((Bullet) obj_at_j).checkExplode ) {
                        if (obj_at_i.hitbox.intersects(obj_at_j.hitbox)) {
                            ((Bullet) obj_at_j).setExplosion(false);
                            obj_at_j.collision();
                            obj_at_i.collision();
                        }
                    }
                    if (((obj_at_j instanceof Bullet && obj_at_i instanceof BreakableWall &&
                            !((Bullet) obj_at_j).checkExplode ))) {
                        if (obj_at_i.hitbox.intersects(obj_at_j.hitbox)) {
                            obj_at_j.collision();
                            obj_at_i.collision();
                        }
                    }
                    if (obj_at_i instanceof Tank && obj_at_j instanceof BreakableWall) {
                        Rectangle r1 = ((Tank) obj_at_i).getOffsetBounds();
                        if (r1.intersects(obj_at_j.hitbox)) {
                            ((Tank) obj_at_i).setMove(false);
                        }
                    }
                    if (obj_at_i instanceof BreakableWall && obj_at_j instanceof Tank) {
                        Rectangle r2 = ((Tank) obj_at_j).getOffsetBounds();
                        if (r2.intersects(obj_at_i.hitbox)) {
                            ((Tank) obj_at_j).setMove(false);
                        }
                    }
                    if (obj_at_i instanceof Tank && obj_at_j instanceof PowerUp) {
                        if (obj_at_i.hitbox.intersects(obj_at_j.hitbox)) {
                            if (((PowerUp) obj_at_j).isHealthBoost) {
                                ((Tank) obj_at_i).setHealth(100);
                                System.out.println("health power up picked up");
                                gameObjects.remove(j);
                            }
                            if (((PowerUp) obj_at_j).isSpeedBoost) {
                                ((Tank) obj_at_i).setSpeedBoost(System.currentTimeMillis());
                                ((Tank) obj_at_i).setIfSpeedBoost(true);
                                System.out.println("Speed boost power up picked up");
                                gameObjects.remove(j);
                            }
                        }
                    }
                }
            }
        }
    }

    public void InitializeGame() {
        this.world = new BufferedImage(GameConstants.GAME_WORLD_WIDTH, GameConstants.GAME_WORLD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        BufferedImage t1img = null, t2img = null, bullet_image, background_image, unbreakable_wall_img, breakable_wall_img = null, exp_img, large_explosion_img;
        try {
            t1img = Resources.getSprite("tank1");
            t2img = Resources.getSprite("tank2");

            unbreakable_wall_img = Resources.getSprite("wall");
            Wall.setUnbreakableWallImg(unbreakable_wall_img);

            breakable_wall_img = Resources.getSprite("break1");
            BreakableWall.setImage(breakable_wall_img);

            background_image = Resources.getSprite("floor");
            Wall.setBackgroundImg(background_image);


            bullet_image = Resources.getSprite("bullet");
            Bullet.setImage(bullet_image);

            exp_img = ImageIO.read(getClass().getResource("/resources/animations/Explosion_small.gif"));
            Bullet.setSmallExplosionImage(exp_img);

            large_explosion_img = ImageIO.read(getClass().getResource("/resources/animations/Explosion_large.gif"));
            Bullet.setBigExplosionImg(large_explosion_img);

            GameWorld.winImage = Resources.getSprite("win");
            GameWorld.loseImage = Resources.getSprite("lose");

            PowerUp.setHealthImg(Resources.getSprite("health"));
            PowerUp.setSpeedImg(Resources.getSprite("speed"));

            bg = Resources.getSound("background");
            bg.setLooping();
            bg.setVolume(2.3f);
            bg.play();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        t1 = new Tank(t1SpawnX, t1SpawnY,  t1img);
        t1.setID("t1");
        t2 = new Tank(t2SpawnX, t2SpawnY,  t2img);
        t2.setID("t2");

        for (int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i = i + 320) {
            for (int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j = j + 240) {
                gameObjects.add(new Wall(i, j, true));
            }
        }

        int widthMap = 64;
        int heightMap = 48;
        int[] new_map_array = {
                9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,3,3,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,3,3,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,5,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,3,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,3,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,2,2,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,5,0,0,0,0,0,0,0,0,0,0,0,2,2,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,5,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,3,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,3,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,5,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,3,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,3,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,4,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,4,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
                9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,
        };

        int column = 0;
        int idx = 0;
        for (int i = 0; i < heightMap; i++) {
            for (int j = 0; j < widthMap; j++) {
                if (column == heightMap) {
                    column = 0;
                }
                int temp_val = new_map_array[idx];
                if (temp_val != 0) {
                    if (temp_val == 2) {
                        gameObjects.add(new BreakableWall(j * 30 , i  * 30, breakable_wall_img));
                    } else if (temp_val == 3) {
                        gameObjects.add(new BreakableWall(j * 30 , i  * 30,  breakable_wall_img));
                    } else if (temp_val == 4) {
                        gameObjects.add(new PowerUp(j * 30, i * 30, false, true));
                    }
                    else if (temp_val == 5) {
                        gameObjects.add(new PowerUp(j * 30, i * 30, true, false));
                    }else if (temp_val == 9)  {
                        gameObjects.add(new Wall(j * 30, i * 30, false));
                    }
                }
                column++;
                idx++;
            }
        }
        PowerUp power1 = new PowerUp(780, 750, true, false);
        PowerUp power2 = new PowerUp(682, 750, true, false);
        gameObjects.add(power1);
        gameObjects.add(power2);
        PowerUp power3 = new PowerUp(730, 852, false, true);
        gameObjects.add(power3);

        TankControl tc1 = new TankControl(t1, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
        TankControl tc2 = new TankControl(t2, KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);

        gameObjects.add(t1);
        t1.setGW(this);
        this.lf.getJf().addKeyListener(tc1);
        gameObjects.add(t2);
        t2.setGW(this);
        this.lf.getJf().addKeyListener(tc2);
    }

    void addGameObject(GameObject obj) {
        this.gameObjects.add(obj);
    }
    public void drawFloor(Graphics2D buffer){
        BufferedImage floor = Resources.getSprite("floor");
        for (int i = 0; i < GameConstants.GAME_WORLD_WIDTH; i+=320){
            for (int j = 0; j < GameConstants.GAME_WORLD_HEIGHT; j+=240){
                buffer.drawImage(floor,i,j,null);
            }
        }
    }

    private void renderMiniMap(Graphics2D g2, BufferedImage world) {
        BufferedImage mm = world.getSubimage(0,0,GameConstants.GAME_WORLD_WIDTH,GameConstants.GAME_WORLD_HEIGHT);
        g2.scale(.2,.2);
        g2.drawImage(mm,
                (GameConstants.GAME_SCREEN_WIDTH*5)/2-(GameConstants.GAME_SCREEN_WIDTH/2)-350,
                (GameConstants.GAME_SCREEN_HEIGHT*5)-(GameConstants.GAME_SCREEN_HEIGHT)-660,null);
    }

    private void renderSplitScreens(Graphics2D g2, BufferedImage world) {
        int t1_x_Coord = (int) t1.getX();
        int t2_x_Coord = (int) t2.getX();
        int t1_y_Coord = (int) t1.getY();
        int t2_y_Coord = (int) t2.getY();

        if (t1_x_Coord < GameConstants.GAME_SCREEN_WIDTH / 4) {
            t1_x_Coord = GameConstants.GAME_SCREEN_WIDTH / 4;
        }
        if (t2_x_Coord < GameConstants.GAME_SCREEN_WIDTH / 4) {
            t2_x_Coord = GameConstants.GAME_SCREEN_WIDTH / 4;
        }
        if (t1_x_Coord > GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4) {
            t1_x_Coord = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4;
        }
        if (t2_x_Coord > GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4) {
            t2_x_Coord = GameConstants.GAME_WORLD_WIDTH - GameConstants.GAME_SCREEN_WIDTH / 4;
        }
        if (t1_y_Coord < GameConstants.GAME_SCREEN_HEIGHT / 2) {
            t1_y_Coord = GameConstants.GAME_SCREEN_HEIGHT / 2;
        }
        if (t2_y_Coord < GameConstants.GAME_SCREEN_HEIGHT / 2) {
            t2_y_Coord = GameConstants.GAME_SCREEN_HEIGHT / 2;
        }
        if (t1_y_Coord > GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT / 2) {
            t1_y_Coord = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT / 2;
        }
        if (t2_y_Coord > GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT / 2) {
            t2_y_Coord = GameConstants.GAME_WORLD_HEIGHT - GameConstants.GAME_SCREEN_HEIGHT / 2;
        }

        BufferedImage left_split_screen = world.getSubimage(t1_x_Coord - GameConstants.GAME_SCREEN_WIDTH / 4,
                t1_y_Coord - GameConstants.GAME_SCREEN_HEIGHT / 2,
                GameConstants.GAME_SCREEN_WIDTH / 2,
                GameConstants.GAME_SCREEN_HEIGHT);
        BufferedImage right_split_screen = world.getSubimage(t2_x_Coord - GameConstants.GAME_SCREEN_WIDTH / 4,
                t2_y_Coord - GameConstants.GAME_SCREEN_HEIGHT / 2,
                GameConstants.GAME_SCREEN_WIDTH / 2,
                GameConstants.GAME_SCREEN_HEIGHT);

        g2.drawImage(left_split_screen, 0, 0, null);
        g2.drawImage(right_split_screen, GameConstants.GAME_SCREEN_WIDTH/2+5, 0, null);

        g2.drawImage(world, GameConstants.GAME_SCREEN_WIDTH / 2 - GameConstants.GAME_WORLD_WIDTH/6/2,
                GameConstants.GAME_SCREEN_HEIGHT - GameConstants.GAME_WORLD_HEIGHT/6,
                GameConstants.GAME_WORLD_WIDTH / 6,
                GameConstants.GAME_WORLD_HEIGHT / 6, null);

        g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
        g2.setColor(Color.YELLOW);
        g2.drawString("Player1 - " + this.t1NumLives + " lives", 20, 30);
        g2.fillRect(20, 40, 2 * t1.getHealth(), 20);

        g2.setColor(new Color(158, 122, 226, 255));
        g2.drawString("Player2 - " + this.t2NumLives + " lives", GameConstants.GAME_SCREEN_WIDTH/2+20, 30);
        g2.fillRect(GameConstants.GAME_SCREEN_WIDTH / 2 + 20, 40, 2 * t2.getHealth(), 20);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Graphics2D buffer = world.createGraphics();
        this.drawFloor(buffer);

        for (int i = 0; i < gameObjects.size(); i++) {
            gameObjects.get(i).drawImage(buffer);
        }
        this.t1.drawImage(buffer);
        this.t2.drawImage(buffer);
        this.activeAnimations.forEach(a -> a.drawImage(buffer));

        renderSplitScreens(g2, world);
        renderMiniMap(g2, world);

        if (t1Win) {
            g2.drawImage(winImage, 0, 0,
                    GameConstants.GAME_SCREEN_WIDTH * 5,  GameConstants.GAME_SCREEN_HEIGHT * 5, null);
        }
        if (t2Win) {
            g2.drawImage(loseImage, 0, 0,
                    GameConstants.GAME_SCREEN_WIDTH * 5, GameConstants.GAME_SCREEN_HEIGHT * 5, null);
        }
    }
}