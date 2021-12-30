package src.gui;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import src.Engine;
import src.GUI;
import src.Window;
import src.gpc.Markdown;
import src.gpc.Syntax;
import src.gpc.LoadingBar;

public class Info extends GUI implements Start {

    public static enum Tab {
        TUTORIAL, CREDITS, SETTINGS;
    }

    private final String path = System.getProperty("user.dir");
    private Font font;
    private Background wallpaper;
    private Tab info;
    private Markdown md;
    private LoadingBar load;
    private BufferedImage[] pfps;

    public Info(Background wallpaper, Tab info) {
        try {
            this.wallpaper = wallpaper;
            this.info = info;
            this.font = Font.createFont(Font.PLAIN, new File(path + "\\rsc\\fonts\\NexaHeavy.ttf"));
            switch(this.info) {
                case TUTORIAL:
                    loadMarkdown(new File(path + "\\rsc\\tutorial.md"));
                    break;
                case CREDITS:
                    loadMarkdown(new File(path + "\\rsc\\credits.md"));
                    pfps = new BufferedImage[4];
                    for (int i = 1; i <= 4; i++)
                        pfps[i-1] = ImageIO.read(new File(path + "\\rsc\\pfp" + i + ".png"));
                    break;
                case SETTINGS:
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMarkdown(File file) throws FileNotFoundException {
        this.md = null;
        Thread load = new Thread() {
            public void run() {
                try {
                    URL[] imgs = Markdown.getImages(file);
                    if (imgs.length > 0) {
                        
                    }
                    md = new Markdown(new Dimension(Window.WIDTH/2, Window.HEIGHT-40), font.deriveFont(16f), file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        load.start();
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
        if (info == Tab.CREDITS) {
            for (int i = 0; i < pfps.length; i++) {
                Dimension m = new Dimension(Window.WIDTH/4, Window.HEIGHT/2);
                Point p = new Point(i / 2 == 0 ? 0 : m.width * 3, i % 2 * m.height);
                int s = m.height / 2;
                g.drawImage(pfps[i], p.x + (m.width - s) / 2, p.y + (m.height - s) / 2, s, s, null);
            }
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
        if (e.getKeyCode() == KeyEvent.VK_DOWN && info == Tab.TUTORIAL)
            md.scroll(2);
        else if (e.getKeyCode() == KeyEvent.VK_UP && info == Tab.TUTORIAL)
            md.scroll(-2);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        md.scroll(e.getWheelRotation() * 4);
    }

    public void mousePressed(MouseEvent e) {
        HashMap<URL, Rectangle> links = md.getLinks();
        int x = (Window.WIDTH - md.getWidth())/2, y = (Window.HEIGHT - md.getHeight())/2;
        if ((e.getX() > x && e.getX() < x + md.getWidth()) && (e.getY() > y && e.getY() < y + md.getHeight())) {
            int mx = e.getX() - x, my = e.getY() - y;
            for (URL link : links.keySet()) {
                Rectangle r = links.get(link);
                if (mx < r.getX() || mx > r.getX() + r.getWidth())
                    continue;
                if (my < r.getY() || my > r.getY() + r.getHeight())
                    continue;
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(link.toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

}
