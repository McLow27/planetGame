package src.gui;

import java.io.File;
import javax.imageio.ImageIO;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Random;
import java.util.Comparator;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Polygon;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Paint;
import java.awt.GradientPaint;
import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.image.BufferedImage;
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

    static record Button(String title, Image icon) {
        public static final int square = 60, space = square/5, rectangle = 4 * square, total = square + space + rectangle;

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
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
            int cs[][] = {{0, carve}, {0, carve*2}, {carve*5, carve*2}, {carve*7, 0}, {carve*6, 0}, {carve*5, carve}}, xs[] = new int[cs.length], ys[] = new int[cs.length];
            for (int i = 0; i < cs.length; i++){
                xs[i] = cs[i][0];
                ys[i] = cs[i][1];
            }
            g.fillPolygon(new Polygon(xs, ys, cs.length));
            for (int i = 0; i < cs.length; i++) {
                xs[i] = size.width - xs[i];
                ys[i] = size.height - ys[i];
            }
            g.fillPolygon(new Polygon(xs, ys, cs.length));
            return img;
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
            buttons.add(new Button("Join Lobby"));
            buttons.add(new Button("Open Lobby"));
            BufferedImage cog = ImageIO.read(new File(path + "\\rsc\\icons\\cog.png")),
                    globe = ImageIO.read(new File(path + "\\rsc\\icons\\globe.png"));
            buttons.add(new Button("Settings", cog));
            buttons.add(new Button("Credits", globe));
            buttons.add(new Button(Window.TITLE + " in a nutshell"));
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
        int startx = (Window.WIDTH - Button.total)/2;
        int starty = (Window.HEIGHT - ((int)(buttons.size()/2)*Button.square + ((int)(buttons.size()/2)-1)*Button.space))/2;
        int nexty = starty;
        final Color color1 = new Color(238, 0, 253), color2 = new Color(168, 0, 244);
        Button btn;
        for (int i = 0; i < buttons.size(); i++) {
            btn = buttons.get(i);
            Dimension dim;
            GradientPaint paint;
            if (i % 2 == 0 && i + 1 == buttons.size()) {
                dim = new Dimension(Button.total, Button.square);
                paint = new GradientPaint(0, dim.height/2, color1,dim.width,dim.height/2,color2,true);
                g.drawImage(btn.renderButton(dim, paint), startx, nexty, null);
                nexty += Button.square + Button.space;
            }
            else if(i % 2 == 0) {
                dim = new Dimension(i % 4 == 2 ? Button.square:Button.rectangle, Button.square);
                paint = new GradientPaint(0, dim.height/2, color1,dim.width,dim.height/2,color2,true);
                g.drawImage(btn.renderButton(dim, paint), startx, nexty, null);
            }
            else if (i%2==1) {
                dim = new Dimension(i % 4 == 1 ? Button.square:Button.rectangle, Button.square);
                paint = new GradientPaint(0, dim.height/2, color1,dim.width,dim.height/2,color2,true);
                g.drawImage(btn.renderButton(dim, paint), startx + Button.space + (i%4==1?Button.rectangle:Button.square), nexty, null);
                nexty += Button.square + Button.space;
            }
        }
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
