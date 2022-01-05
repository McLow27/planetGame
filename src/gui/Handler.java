package src.gui;

import java.util.LinkedList;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.MouseWheelEvent;
import src.GUI;
import src.Window;
import src.obj.Entity;
import src.srv.ServerInterface;
import src.gpc.Map;

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
    private ServerInterface client;
    private LinkedList<Entity> objects;
    private Map map;

    public Handler(ServerInterface client) {
        this.client = client;
        this.objects = new LinkedList<Entity>();
        this.map = new Map(new Dimension(Window.WIDTH, Window.HEIGHT), Map.Celestial.assign(client.getMap()));
    }

    @Override
    public void tick() {
        for (Entity object : objects)
            object.tick();
    }

    @Override
    public void render(Graphics g) {
        render(g, 0.0);
    }

    @Override
    public void render(Graphics g, double d) {
        g.drawImage(map.render(), 0, 0, null);

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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0)
            map.zoomOut();
        else if (e.getWheelRotation() < 0)
            map.zoomIn();
    }
}
