package src;

import java.util.LinkedList;
import java.awt.Graphics;

public class Handler {
    LinkedList<Entity> objects = new LinkedList<Entity>();

    public void tick() {
        for (Entity object : objects)
            object.tick();
    }

    public void render(Graphics g) {
        for (Entity object : objects) {
            object.render(g);
        }
    }

    public void addObject(Entity object) {
        objects.add(object);
    }

    public void removeObject(Entity object) {
        objects.remove(object);
    }
}
