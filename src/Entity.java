package src;

import java.awt.Graphics;

import src.tech.Type;

public abstract class Entity {
    
    protected int x, y;
    protected Type type;

    public Entity(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public abstract void tick();

    public abstract void render(Graphics g);

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Type getType() {
        return type;
    }

}
