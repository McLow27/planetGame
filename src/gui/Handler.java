package src.gui;

import java.util.LinkedList;
import java.awt.Graphics;
import src.GUI;
import src.obj.Entity;

/**
 * A class for handling all the objects of the game.
 * 
 * Note that the ticking mechanism will be disabled in the event of a real-time encounter 
 * and will instead be replaced by repeatedly requesting the current game state from the server 
 * which will calculate everything instead.
 * 
 * @author TheCommandBlock
 * @since 03/12/2021
 */
public class Handler extends GUI {
    LinkedList<Entity> objects = new LinkedList<Entity>();

    @Override
    public void tick() {
        for (Entity object : objects)
            object.tick();
    }

    @Override
    public void render(Graphics g) {
        for (Entity object : objects) {
            object.render(g);
        }
    }

    /**
     * Adds a new entity to the game
     * 
     * @param object the {@link src.obj.Entity} to add
     */
    public void addObject(Entity object) {
        objects.add(object);
    }

    /**
     * Removes an entity from the game
     * 
     * @param object the {@link src.obj.Entity} to remove
     */
    public void removeObject(Entity object) {
        objects.remove(object);
    }
}
