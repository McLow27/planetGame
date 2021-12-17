package src.gui;

import java.io.File;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.util.Random;
import java.util.HashMap;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Canvas;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Image;
import java.util.Comparator;
import src.Window;
import src.GUI;

public class Title extends GUI {

    static final String path = System.getProperty("user.dir");
    private Random random = new Random();
    private Font font;
    private static BufferedImage[] sats;

    static class Satellite {
        private static final Random random = new Random();
        private double x, y, r;
        private double velX;
        private Dimension dimension;
        private int sat;

        public Satellite() {
            this.velX = (random.nextInt(50) + 10) / 40.0;
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

    static record Button(String title, Dimension bounds, Image icon) {
        public Button(String title, Dimension bounds) {
            this(title, bounds, null);
        }

        public int getWidth() {
            return bounds.width;
        }

        public int getHeight() {
            return bounds.height;
        }

        public String getTitle() {
            return title;
        }

        public Image getIcon() {
            return icon;
        }
    }

    BufferedImage wallpaper;
    LinkedList<Satellite> satellites;
    LinkedList<Rectangle> flares;
    BufferedImage header;
    LinkedList<Button> buttons;

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
            String fontfile = new String[] { "AllertaStencil", "Audiowide", "Baumans", "Gugi", "Orbitron",
                    "Righteous" }[5];
            font = Font.createFont(Font.PLAIN, new File(path + "\\rsc\\fonts\\" + fontfile + ".ttf")).deriveFont(92f);
            FontMetrics fm = new Canvas().getFontMetrics(font);
            final int offset = 4;
            header = new BufferedImage(fm.stringWidth(Window.TITLE) + offset, fm.getHeight() + fm.getDescent() + offset,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = header.getGraphics();
            final Color front = new Color(254, 251, 62), back = new Color(71, 233, 235);
            g.setFont(font);
            g.setColor(back);
            g.drawString(Window.TITLE, offset, header.getHeight() - fm.getDescent());
            g.setColor(front);
            g.drawString(Window.TITLE, 0, header.getHeight() - fm.getDescent() + offset);
            // Buttons
            buttons = new LinkedList<Button>();
            buttons.add(new Button("Join Lobby", new Dimension(4 * 60, 60)));
            buttons.add(new Button("Open Lobby", new Dimension(4 * 60, 60)));
            BufferedImage cog = ImageIO.read(new File(path + "\\rsc\\icons\\cog.png")),
                    globe = ImageIO.read(new File(path + "\\rsc\\icons\\globe.png"));
            buttons.add(new Button("Settings", new Dimension(60, 60), cog));
            buttons.add(new Button("Credits", new Dimension(60, 60), globe));
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
        Button btn;
    }

    public void tick() {
        if (random.nextInt(200) < 1)
            satellites.add(new Satellite());

        for (int i = 0; i < satellites.size(); i++) {
            Satellite sat = satellites.get(i);
            sat.setX(sat.getX() + sat.getXVelocity());
            if (sat.getX() > Window.WIDTH)
                satellites.remove(i);
        }
    }
}
