package src.gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import src.srv.ServerInterface;
import src.GUI;
import src.Engine;

public class Lobby extends GUI implements Start {

    private Background wallpaper;
    private ServerInterface player;

    public Lobby(Background wallpaper, ServerInterface player) {
        this.wallpaper = wallpaper;
        this.player = player;
    }

    public void render(Graphics g) {
        simRender(g, 0.0);
    }

    public void simRender(Graphics g, double d) {
        this.wallpaper.simRender(g, d);
    }

    public void tick() {
        this.wallpaper.tick();
        // XXX Just to get the "unused" warning to disappear
        player.disconnect();
    }

    public Background getWallpaper() {
        return wallpaper;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            Engine.getEngine().setState(Engine.UI.State.TITLE);
    }
    
}
