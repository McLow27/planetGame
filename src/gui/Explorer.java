package src.gui;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.File;
import src.Engine;
import src.GUI;
import src.Window;
import src.gpc.LoadingBar;
import src.gpc.Panel;

/**
 * An explorer to find open lobbies.
 * 
 * @author TheCommandBlock
 * @since 03/12/2021
 */
public class Explorer extends GUI implements Start {

    private Background wallpaper;
    private Panel panel;

    public Explorer(Background wallpaper) {
        this.wallpaper = wallpaper;
        try {
            this.panel = new LoadingBar(new Dimension(600, 60), Font.createFont(Font.PLAIN,
            new File(System.getProperty("user.dir") + "\\rsc\\fonts\\NexaHeavy.ttf")).deriveFont(24f),
            20 * 8, "Gathering lobbies...");
            ((LoadingBar) this.panel).finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(Graphics g) {
        render(g, 0.0);
    }

    @Override
    public void render(Graphics g, double d) {
        this.wallpaper.render(g, d);
        if (this.panel != null && (this.panel instanceof LoadingBar ? !((LoadingBar) this.panel).finished() : true))
            g.drawImage(this.panel.render(d), (Window.WIDTH - this.panel.getWidth()) / 2, (Window.HEIGHT - this.panel.getHeight()) / 2, null);
    }

    @Override
    public void tick() {
        this.wallpaper.tick();
        if (this.panel != null && (this.panel instanceof LoadingBar && !((LoadingBar) this.panel).finished()))
            this.panel.tick();
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
