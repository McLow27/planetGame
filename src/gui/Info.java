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
import java.awt.FontFormatException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import src.Engine;
import src.GUI;
import src.Window;
import src.gpc.Markdown;
import src.gpc.LoadingBar;

/**
 * A class combining the GUIs for the tutorial, the credits and the settings.
 * 
 * @author TheCommandBlock
 * @since 22/12/2021
 */
public class Info extends GUI implements Start {

    /**
     * Constants representing the possible states of this GUI
     */
    public static enum Tab {
        TUTORIAL, CREDITS, SETTINGS;
    }

    /**
     * The path of this executable
     */
    private final String path = System.getProperty("user.dir");
    /**
     * The font for everything here
     */
    private Font font;
    /**
     * The wallpaper background
     */
    private Background wallpaper;
    /**
     * An enum constant defining what exactly is being shown right now
     */
    private Tab info;
    /**
     * A Markdown object displaying a syntax with various elements
     */
    private Markdown md;
    /**
     * A loading bar to show while the {@link src.gpc.Markdown} is being compiled and all resources loaded from the internet
     */
    private LoadingBar load;
    /**
     * An array of {@link java.awt.image.BufferedImage}'s showing the profile pictures of the developers
     */
    private BufferedImage[] pfps;

    /**
     * Creates a new GUI that displays either the tutorial, the credits or the settings.
     * 
     * @param wallpaper the animated background of the title screen
     * @param info      an enum constant indicating what GUI to show
     */
    public Info(Background wallpaper, Tab info) {
        try {
            this.wallpaper = wallpaper;
            this.info = info;
            this.font = Font.createFont(Font.PLAIN, new File(path + "\\rsc\\fonts\\NexaHeavy.ttf"));
            switch(this.info) {
                case TUTORIAL:
                    // A simple markdown
                    loadMarkdown(new File(path + "\\rsc\\tutorial.md"));
                    break;
                case CREDITS:
                    // A markdown and four profile pictures
                    loadMarkdown(new File(path + "\\rsc\\credits.md"));
                    pfps = new BufferedImage[4];
                    for (int i = 1; i <= 4; i++)
                        pfps[i-1] = ImageIO.read(new File(path + "\\rsc\\pfp" + i + ".png"));
                    break;
                case SETTINGS:
                    // TODO nothing yet but hopefully some settings soon
                    break;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a markdown file in a second thread and shows a loading bar while its compiling
     * 
     * @param file the markdown file
     * @throws FileNotFoundException when loading the {@link java.awt.Font}
     * @throws FontFormatException ''
     * @throws IOException ''
     */
    public void loadMarkdown(File file) throws FileNotFoundException, FontFormatException, IOException {
        this.md = null;
        this.load = new LoadingBar(new Dimension(600, 60), Font.createFont(Font.PLAIN,
        new File(System.getProperty("user.dir") + "\\rsc\\fonts\\NexaHeavy.ttf")).deriveFont(24f),
        (int) (2.5 * 60));
        Thread secondary = new Thread() {
            public void run() {
                try {
                    Markdown compiled = new Markdown(new Dimension(Window.WIDTH/2, Window.HEIGHT-40), font.deriveFont(16f), file);
                    md = compiled;
                    load.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        secondary.start();
    }

    @Override
    public void render(Graphics g) {
        render(g, 0.0);
    }

    @Override
    public void render(Graphics g, double d) {
        this.wallpaper.render(g, d);
        if (load != null && !load.finished()) {
            g.drawImage(load.render(d), (Window.WIDTH - load.getWidth())/2, (Window.HEIGHT - load.getHeight())/2, null);
        } else if (md != null) {
            BufferedImage rend = md.render();
            g.drawImage(rend, (Window.WIDTH - rend.getWidth())/2, (Window.HEIGHT - rend.getHeight())/2, null);
            if (info == Tab.CREDITS) {
                for (int i = 0; i < pfps.length; i++) {
                    Dimension m = new Dimension(Window.WIDTH/4, Window.HEIGHT/2);
                    Point p = new Point(i / 2 == 0 ? 0 : m.width * 3, i % 2 * m.height);
                    int s = m.height / 2;
                    g.drawImage(pfps[i], p.x + (m.width - s) / 2, p.y + (m.height - s) / 2, s, s, null);
                }
            }
        }
    }

    @Override
    public void tick() {
        this.wallpaper.tick();
        if (load != null && !load.finished())
            load.tick();
    }
    
    public Background getWallpaper() {
        return wallpaper;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
            Engine.setState(Engine.UI.State.TITLE);
        if (e.getKeyCode() == KeyEvent.VK_DOWN && info == Tab.TUTORIAL)
            md.scroll(2);
        else if (e.getKeyCode() == KeyEvent.VK_UP && info == Tab.TUTORIAL)
            md.scroll(-2);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        md.scroll(e.getWheelRotation() * 4);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (md == null) return;
        // Opens the links of the markdown if clicked
        HashMap<URL, Rectangle> links = md.getLinks();
        int x = (Window.WIDTH - md.getWidth())/2, y = (Window.HEIGHT - md.getHeight())/2;
        if ((e.getX() > x && e.getX() < x + md.getWidth()) && (e.getY() > y && e.getY() < y + md.getHeight())) {
            int mx = e.getX() - x, my = e.getY() - y;
            for (URL link : links.keySet()) {
                Rectangle r = links.get(link);
                // Checks whether the mouse is within the bounds of the link
                if (mx < r.getX() || mx > r.getX() + r.getWidth())
                    continue;
                if (my < r.getY() || my > r.getY() + r.getHeight())
                    continue;
                // Attemps to look up the link in the browser
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
