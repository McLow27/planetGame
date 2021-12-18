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
import java.awt.Canvas;
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
import src.Window;
import src.Engine;
import src.GUI;

public class Title extends GUI {

    /**
     * A random number generator for everything in this class
     */
    private Random random = new Random();
    /**
     * A map containing all clickable objects on screen and their identifiers
     */
    private HashMap<String, Rectangle> actionfields;
    /**
     * hfont = the font of the header; font = the font of everything else
     */
    private static Font hfont, font;
    /**
     * The images of all asteroids and artifical satellites flying by in the background
     */
    private static BufferedImage[] sats;
    /**
     * The path of this code, + "\\rsc" is the resource folder
     */
    static final String path = System.getProperty("user.dir");

    static class Satellite {
        private static final Random random = new Random();
        private double x, y, r;
        private double velX;
        private Dimension dimension;
        private int sat;

        public Satellite() {
            this.velX = (random.nextInt(50) + 10) / 12.0;
            this.sat = random.nextInt(sats.length);
            final int width = sats[this.sat].getWidth(), height = sats[this.sat].getHeight();
            final int max = 140, min = 30;
            double s;
            do
                s = random.nextInt(1000) / 750.0;
            while (!(s * width < max && s * width > min));
            this.dimension = new Dimension((int) (width * s), (int) (height * s));
            this.x = -this.dimension.getWidth();
            this.y = random.nextInt(Window.HEIGHT);
            this.r = random.nextInt(360);
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public int getSatellite() {
            return sat;
        }

        public double getRotation() {
            return r;
        }

        public double getXVelocity() {
            return velX;
        }

        public Dimension getDimension() {
            return dimension;
        }

        public void setX(double x) {
            this.x = x;
        }
    }

    static record Button(String title, Image icon) {

        public static final int square = 60, space = square / 5, rectangle = 4 * square,
                total = square + space + rectangle;

        public Button(String title) {
            this(title, null);
        }

        public String getTitle() {
            return title;
        }

        public Image getIcon() {
            return icon;
        }

        public BufferedImage renderButton(Dimension size, Paint color) {
            BufferedImage img = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) img.getGraphics();
            g.setPaint(color);
            g.fillRect(0, 0, size.width, size.height);
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
     * The wallpaper in the background
     */
    BufferedImage wallpaper;
    /**
     * A linked list containing all the asteroids and artificial satellites in the background
     */
    LinkedList<Satellite> satellites;
    /**
     * The tick of the fade-in animation of the buttons
     */
    int fadein;
    /**
     * The UI buttons on the title screen
     */
    LinkedList<Button> buttons;
    /**
     * The header image
     */
    BufferedImage header;
    /**
     * Random transparent rectangles that add a futuristic effect to the header's fade-in animation
     */
    LinkedList<Rectangle> flares;

    public Title() {
        try {
            // Wallpaper
            wallpaper = ImageIO.read(new File(path + "\\rsc\\title.png"));
            // Background satellites
            int len = new File(path + "\\rsc\\satellites\\").listFiles().length;
            sats = new BufferedImage[len];
            for (int i = 0; i < len; i++) {
                sats[i] = ImageIO.read(new File(path + "\\rsc\\satellites\\satellite" + i + ".png"));
            }
            satellites = new LinkedList<Satellite>();
            int r = random.nextInt(8) + 6;
            for (int i = 0; i < r; i++) {
                Satellite sat = new Satellite();
                sat.setX(random.nextInt(Window.WIDTH));
                satellites.add(sat);
            }
            // Title and animation
            hfont = Font.createFont(Font.PLAIN, new File(path + "\\rsc\\fonts\\Righteous.ttf")).deriveFont(92f);
            FontMetrics fm = new Canvas().getFontMetrics(hfont);
            final int offset = 4;
            header = new BufferedImage(fm.stringWidth(Window.TITLE) + offset, fm.getHeight() + fm.getDescent() + offset,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = header.getGraphics();
            final Color front = new Color(254, 251, 62), back = new Color(71, 233, 235);
            g.setFont(hfont);
            g.setColor(back);
            g.drawString(Window.TITLE, offset, header.getHeight() - fm.getDescent());
            g.setColor(front);
            g.drawString(Window.TITLE, 0, header.getHeight() - fm.getDescent() + offset);
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
            // Fade-in animations
            fadein = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics g) {
        simRender(g, 0.0);
    }

    public void simRender(Graphics g, double d) {
        // Wallpaper
        int w = wallpaper.getWidth(), h = wallpaper.getHeight();
        if (w / Window.WIDTH < h / Window.HEIGHT) {
            h = h * Window.WIDTH / w;
            w = Window.WIDTH;
        } else {
            w = w * Window.HEIGHT / h;
            h = Window.HEIGHT;
        }
        g.drawImage(wallpaper, (Window.WIDTH - w) / 2, (Window.HEIGHT - h) / 2, w, h, null);

        // Satellites
        // TODO find out how to rotate the satellite
        for (Satellite sat : satellites) {
            g.drawImage(sats[sat.getSatellite()], (int) (sat.getX() + sat.getXVelocity() * d),
                    (int) sat.getY(),
                    (int) sat.getDimension().getWidth(),
                    (int) sat.getDimension().getHeight(), null);
        }

        // Title
        g.drawImage(header, (Window.WIDTH - header.getWidth()) / 2, 40, null);

        // UI Buttons
        Graphics2D g2d = (Graphics2D) g;
        final Composite neutral = g2d.getComposite();
        final Color color1 = new Color(238, 0, 253), color2 = new Color(168, 0, 244);
        for (int i = 0; i < buttons.size(); i++) {
            final double ticks = 30.0;
            if (i * ticks / 2 > fadein)
                continue;
            double fade = 1.0;
            if(fadein < (i + 2) * ticks / 2)
                fade = smoothCurve((int) (fadein - i * ticks / 2), ticks, 100) / 100.0;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) fade));
            Rectangle field = actionfields.get(buttons.get(i).getTitle());
            GradientPaint paint = new GradientPaint(0, field.height / 2, color1, field.width, field.height / 2, color2,
                    true);
            g.drawImage(buttons.get(i).renderButton(new Dimension(field.width, field.height), paint), field.x,
                    field.y + (int)((1.0-fade) * Button.square / 2), null);
        }
        g2d.setComposite(neutral);
    }

    public void tick() {
        if (random.nextInt(60) < 1)
            satellites.add(new Satellite());
        for (int i = 0; i < satellites.size(); i++) {
            Satellite sat = satellites.get(i);
            sat.setX(sat.getX() + sat.getXVelocity());
            if (sat.getX() > Window.WIDTH)
                satellites.remove(i);
        }

        // Fade-in animation
        if (fadein < 30 * (buttons.size()+1))
            fadein++;

        // Hover action
        Point mouse = Engine.getMousePoint();
        for (String title : actionfields.keySet()) {
            Rectangle field = actionfields.get(title);
            if (mouse.getX() < field.getX() || mouse.getX() > field.getX() + field.getWidth())
                continue;
            if (mouse.getY() < field.getY() || mouse.getY() > field.getY() + field.getHeight())
                continue;
            // TODO Do something
            System.out.println("Mouse is hovering over '" + title + "'!");
        }
    }

    public void mousePressed(MouseEvent e) {
        for (String title : actionfields.keySet()) {
            Rectangle field = actionfields.get(title);
            if (e.getX() < field.getX() || e.getX() > field.getX() + field.getWidth())
                continue;
            if (e.getY() < field.getY() || e.getY() > field.getY() + field.getHeight())
                continue;
            // TODO Add whatever happens now
            System.out.println("'" + title + "' has been clicked!");
        }
    }
}
