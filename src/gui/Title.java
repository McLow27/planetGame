package src.gui;

import java.io.File;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Random;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Polygon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.GradientPaint;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import src.Window;
import src.gpc.Header;
import src.Engine;
import src.GUI;

/**
 * A new title screen for the game with fancy animation and buttons to all the other slides.
 * 
 * @author TheCommandBlock
 * @since 15/12/2021
 */
public class Title extends GUI implements Start {

    /**
     * A random number generator for everything in this class
     */
    private Random random = new Random();
    /**
     * A map containing all clickable objects on screen and their identifiers
     */
    private HashMap<String, Rectangle> actionfields;
    /**
     * The standard Kurzgesagt font for everything
     */
    private static Font font;
    /**
     * The path of this code, + "\\rsc" is the resource folder
     */
    static final String path = System.getProperty("user.dir");

    /**
     * A record of final attributes representing a button on the screen
     */
    static record Button(String title, Image icon) {

        /**
         * Constant values for calculating the rendering of the button
         */
        public static final int square = 60, space = square / 5, rectangle = 4 * square,
                total = square + space + rectangle;

        /**
         * A new button with only a title. Since icon buttons should still have a title, 
         * there is no constructor for only an icon.
         * 
         * @param title a string that will be rendered onto the button
         */
        public Button(String title) {
            this(title, null);
        }

        /**
         * A new button with an icon and optionally a title.
         * 
         * @param title a title that could be shown as hover action
         * @param icon  an image that will be rendered onto the button
         */
        public Button(String title, Image icon) {
            this.title = title;
            this.icon = icon;
        }

        /**
         * Gets the title of the button
         * 
         * @return the title string
         */
        public String getTitle() {
            return title;
        }

        /**
         * Gets the icon of the button
         * 
         * @return an {@link java.awt.Image} icon
         */
        public Image getIcon() {
            return icon;
        }

        /**
         * Renders the button according to all its attributes either with an icon or a title.
         * 
         * @param size  the dimensions of the button
         * @param color the {@link java.awt.Paint} with which to draw the background
         * @return a rendered {@link java.awt.image.BufferedImage} of the button
         */
        public BufferedImage renderButton(Dimension size, Paint color) {
            BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) img.getGraphics();
            // Draw the background
            g.setPaint(color);
            g.fillRect(0, 0, size.width, size.height);
            // Remove portions in the upper left and lower right corners
            final int carve = Button.square / 12;
            final Composite neutral = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
            int cs[][] = { { 0, carve }, { 0, carve * 2 }, { (int) (carve * 5.5), carve * 2 },
                    { (int) (carve * 7.5), 0 }, { carve * 6, 0 },
                    { carve * 5, carve } }, xs[] = new int[cs.length], ys[] = new int[cs.length];
            for (int i = 0; i < cs.length; i++) {
                xs[i] = cs[i][0];
                ys[i] = cs[i][1];
            }
            g.fillPolygon(new Polygon(xs, ys, cs.length));
            for (int i = 0; i < cs.length; i++) {
                xs[i] = size.width - xs[i];
                ys[i] = size.height - ys[i];
            }
            g.fillPolygon(new Polygon(xs, ys, cs.length));
            // Draw the icon or the title onto the button
            g.setComposite(neutral);
            if (icon != null)
                g.drawImage(icon, (size.width - carve * 6) / 2, carve * 3, Button.square - carve * 6,
                        Button.square - carve * 6, null);
            else {
                g.setColor(Color.WHITE);
                g.setFont(font.deriveFont(22f));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(title, (size.width - fm.stringWidth(title)) / 2,
                        (size.height / 2 + (fm.getHeight() + fm.getDescent())) / 2);
            }
            return img;
        }
    }

    /**
     * The background object with random moving satellites
     */
    private Background wallpaper;
    /**
     * The tick of the fade-in animation of the buttons
     */
    private short fadein;
    /**
     * The UI buttons on the title screen
     */
    private LinkedList<Button> buttons;
    /**
     * 
     */
    private short hoverflow[];
    /**
     * The header image
     */
    private Header header;
    /**
     * The third color for the hover animation of the GUI buttons
     */
    private Color color3;

    public Title() {
        this(new Background());
    }

    /**
     * Creates a new title screen with buttons and header plus fancy build-up animation
     * 
     * @param wallpaper
     */
    public Title(Background wallpaper) {
        try {
            // Wallpaper
            this.wallpaper = wallpaper;
            // Random third color
            Color[] colors = new Color[] {new Color(11, 255, 131), new Color(255, 254, 6), new Color(25, 254, 255)};
            color3 = colors[random.nextInt(colors.length)];
            // Title and animation
            final Color front = new Color(254, 251, 62), back = new Color(71, 233, 235);
            header = new Header(Window.TITLE, Font.createFont(Font.PLAIN, new File(path + "\\rsc\\fonts\\Righteous.ttf")).deriveFont(92f), front, back);
            // Buttons
            font = Font.createFont(Font.PLAIN, new File(path + "\\rsc\\fonts\\NexaHeavy.ttf"));
            buttons = new LinkedList<Button>();
            BufferedImage cog = ImageIO.read(new File(path + "\\rsc\\icons\\cog.png")),
                    globe = ImageIO.read(new File(path + "\\rsc\\icons\\globe.png"));
            buttons.add(new Button("Join Lobby"));
            buttons.add(new Button("Settings", cog));
            buttons.add(new Button("Credits", globe));
            buttons.add(new Button("Open Lobby"));
            buttons.add(new Button(Window.TITLE + " in a nutshell"));
            // Button fields
            int startx = (Window.WIDTH - Button.total) / 2;
            int starty = (Window.HEIGHT
                    - ((int) (buttons.size() / 2) * Button.square + ((int) (buttons.size() / 2) - 1) * Button.space))
                    / 2;
            int nexty = starty;
            actionfields = new HashMap<String, Rectangle>();
            for (int i = 0; i < buttons.size(); i++) {
                Rectangle box;
                if (i % 2 == 0 && i + 1 == buttons.size()) {
                    box = new Rectangle(startx, nexty, Button.total, Button.square);
                    nexty += Button.square + Button.space;
                } else if (i % 2 == 0) {
                    box = new Rectangle(startx, nexty, i % 4 == 2 ? Button.square : Button.rectangle, Button.square);
                } else if (i % 2 == 1) {
                    box = new Rectangle(startx + Button.space + (i % 4 == 1 ? Button.rectangle : Button.square), nexty,
                            i % 4 == 1 ? Button.square : Button.rectangle, Button.square);
                    nexty += Button.square + Button.space;
                } else {
                    box = null;
                }
                actionfields.put(buttons.get(i).getTitle(), box);
            }
            // Button animations
            this.fadein = 0;
            this.hoverflow = new short[buttons.size()];
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
        // Wallpaper
        this.wallpaper.render(g, d);
        
        // Title
        final BufferedImage titlerender = header.render();
        g.drawImage(titlerender, (Window.WIDTH - titlerender.getWidth()) / 2, 40, null);

        // UI Buttons
        Graphics2D g2d = (Graphics2D) g;
        final Composite neutral = g2d.getComposite();
        final Color color1 = new Color(238, 0, 253), color2 = new Color(134, 0, 105);
        for (int i = 0; i < buttons.size(); i++) {
            final double ticks = 30.0;
            if (i * ticks / 2 > fadein)
                continue;
            double fade = 1.0;
            if(fadein < (i + 2) * ticks / 2)
                fade = smoothCurve((int) (fadein - i * ticks / 2), ticks, 100) / 100.0;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) fade));
            Rectangle field = actionfields.get(buttons.get(i).getTitle());
            if (hoverflow[i] > 0 && fade >= 1.0) {
                GradientPaint paint;
                final int shift = (int)(hoverflow[i] / 30.0 * field.width);
                if (i % 2 == 0) {
                    paint = new GradientPaint(shift, field.height / 2, color1, field.width + shift, field.height / 2, color2, false);
                    g.drawImage(buttons.get(i).renderButton(new Dimension(field.width, field.height), paint), field.x, field.y, null);
                    paint = new GradientPaint(-(field.width - shift), field.height / 2, color3, shift, field.height / 2, color1, false);
                    g.drawImage(buttons.get(i).renderButton(new Dimension(field.width, field.height), paint).getSubimage(0, 0, shift, field.height), field.x, field.y, null);
                } else {
                    paint = new GradientPaint(-shift, field.height / 2, color1, field.width - shift, field.height / 2, color2, false);
                    g.drawImage(buttons.get(i).renderButton(new Dimension(field.width, field.height), paint), field.x, field.y, null);
                    paint = new GradientPaint(field.width - shift, field.height / 2, color2, 2 * field.width - shift, field.height / 2, color3, false);
                    g.drawImage(buttons.get(i).renderButton(new Dimension(field.width, field.height), paint).getSubimage(field.width - shift, 0, shift, field.height), field.x + field.width - shift, field.y, null);
                }
            } else {
                GradientPaint paint = new GradientPaint(0, field.height / 2, color1, field.width, field.height / 2, color2, true);
                g.drawImage(buttons.get(i).renderButton(new Dimension(field.width, field.height), paint), field.x,
                        field.y + (int)((1.0-fade) * Button.square / 2), null);
            }
        }
        g2d.setComposite(neutral);
    }

    @Override
    public void tick() {
        // Satellite wallpaper
        this.wallpaper.tick();

        // Fade-in animation
        header.tick();
        if (fadein < 30 * (buttons.size()+1)) {
            fadein++;
            return;
        }

        // Hover action
        Point mouse = Engine.getMousePoint();
        for (int i = 0; i < buttons.size(); i++) {
            if (hoverflow[i] > 0)
                hoverflow[i]--;
        }
        for (String title : actionfields.keySet()) {
            Rectangle field = actionfields.get(title);
            if (mouse.getX() < field.getX() || mouse.getX() > field.getX() + field.getWidth())
                continue;
            if (mouse.getY() < field.getY() || mouse.getY() > field.getY() + field.getHeight())
                continue;
            int index = -1;
            for (int i = 0; i < buttons.size(); i++) {
                if (buttons.get(i).getTitle() == title) {
                    index = i;
                    break;
                }
            }
            if (index == -1)
                break;
            if (hoverflow[index] < 30)
                hoverflow[index] += 2;
            if (hoverflow[index] > 30)
                hoverflow[index] = 30;
            break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (String title : actionfields.keySet()) {
            Rectangle field = actionfields.get(title);
            if (e.getX() < field.getX() || e.getX() > field.getX() + field.getWidth())
                continue;
            if (e.getY() < field.getY() || e.getY() > field.getY() + field.getHeight())
                continue;
            switch (title) {
                case "Open Lobby":
                case "Join Lobby":
                    Engine.getEngine().setState(Engine.UI.State.EXPLORER);
                    break;
                case Window.TITLE + " in a nutshell":
                    Engine.getEngine().setState(Engine.UI.State.TUTORIAL);
                    break;
                case "Settings":
                    Engine.getEngine().setState(Engine.UI.State.SETTINGS);
                    break;
                case "Credits":
                    Engine.getEngine().setState(Engine.UI.State.CREDITS);
                    break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            // Actually only for maintenance and development purposes
            try{
                final Color front = new Color(254, 251, 62), back = new Color(71, 233, 235);
                header = new Header(Window.TITLE, Font.createFont(Font.PLAIN, new File(path + "\\rsc\\fonts\\Righteous.ttf")).deriveFont(92f), front, back);
                fadein = 0;
                Color[] colors = new Color[] {new Color(11, 255, 131), new Color(255, 254, 6), new Color(25, 254, 255)};
                color3 = colors[random.nextInt(colors.length)];
            } catch(Exception x) {
                x.printStackTrace();
            }
        }
    }

    public Background getWallpaper() {
        return this.wallpaper;
    }
}
