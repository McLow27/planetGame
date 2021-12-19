package src.gui;

import java.util.LinkedList;
import java.awt.Graphics;
import src.GUI;

public class Handler extends GUI {
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
