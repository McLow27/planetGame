package src.gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import src.srv.ServerInterface;
import src.GUI;
import src.Engine;

/**
 * A GUI that will be shown inside a lobby.
 * 
 * @author TheCommandBlock
 * @since 03/12/2021
 */
public class Lobby extends GUI implements Start {

    private Background wallpaper;
    private ServerInterface player;

    public Lobby(Background wallpaper, ServerInterface player) {
        this.wallpaper = wallpaper;
        this.player = player;
    }

    @Override
    public void render(Graphics g) {
        render(g, 0.0);
    }

    @Override
    public void render(Graphics g, double d) {
        this.wallpaper.render(g, d);
    }

    @Override
    public void tick() {
        this.wallpaper.tick();
        // XXX Just to get the "unused" warning to disappear
        player.disconnect();
    }

    public Background getWallpaper() {
        return wallpaper;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            Engine.setState(Engine.UI.State.TITLE);
    }
    
}
