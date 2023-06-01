package tankrotationexample.game;

import java.awt.*;

public abstract class GameObject {
    public float x;
    public float y;
    public float angle;
    public Rectangle hitbox;

    public abstract void update();
    public abstract void drawImage(Graphics g);
    public abstract void collision();
}

