package src.gui;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import src.Engine;
import src.GUI;

public class Info extends GUI implements Start {

    public static enum Markdown {
        TUTORIAL, CREDITS, SETTINGS;
    }

    private Background wallpaper;
    private Markdown info;

    public Info(Background wallpaper, Markdown info) {
        this.wallpaper = wallpaper;
        this.info = info;
        if (this.info == Markdown.TUTORIAL) {

        } else if (this.info == Markdown.CREDITS) {

        }
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
