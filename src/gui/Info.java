package src.gui;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import src.Engine;
import src.GUI;
import src.Window;
import src.gpc.Markdown;

public class Info extends GUI implements Start {

    public static enum Tab {
        TUTORIAL, CREDITS, SETTINGS;
    }

    private final String path = System.getProperty("user.dir");
    private Font font;
    private Background wallpaper;
    private Tab info;
    private Markdown md;


    public Info(Background wallpaper, Tab info) {
        try {
            this.wallpaper = wallpaper;
            this.info = info;
            this.font = Font.createFont(Font.PLAIN, new File(path + "\\rsc\\fonts\\NexaHeavy.ttf"));
            switch(this.info) {
                case TUTORIAL:
                    this.md = new Markdown(new Dimension(Window.WIDTH/2, Window.HEIGHT/2), font.deriveFont(16f), new File(path + "\\rsc\\tutorial.md"));
                    break;
                case CREDITS:
                    // For Mason to do
                    break;
                case SETTINGS:
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics g) {
        simRender(g, 0.0);
    }

    public void simRender(Graphics g, double d) {
        this.wallpaper.simRender(g, d);
        if (md != null) {
            BufferedImage rend = md.render();
            g.drawImage(rend, (Window.WIDTH - rend.getWidth())/2, (Window.HEIGHT - rend.getHeight())/2, null);
        }
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
