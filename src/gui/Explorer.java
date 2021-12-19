package src.gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import src.Engine;
import src.GUI;

public class Explorer extends GUI implements Start {

    private Background wallpaper;

    public Explorer(Background wallpaper) {
        this.wallpaper = wallpaper;
    }

    public void render(Graphics g) {
        simRender(g, 0.0);
    }

    public void simRender(Graphics g, double d) {
        this.wallpaper.simRender(g, d);
    }

    public void tick() {
        this.wallpaper.tick();
    }

    public Background getWallpaper() {
        return wallpaper;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            Engine.getEngine().setState(Engine.UI.State.TITLE);
    }
    
}
