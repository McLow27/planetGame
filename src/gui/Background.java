package src.gui;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Dimension;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Random;
import java.util.LinkedList;
import src.GUI;
import src.Window;

public class Background extends GUI {

    static class Satellite {
        private static final Random random = new Random();
        private double x, y;
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

    /**
     * A random number generator for everything in this class
     */
    private Random random = new Random();
    /**
     * The images of all asteroids and artifical satellites flying by in the background
     */
    private static BufferedImage[] sats;
    /**
     * The path of this code, + "\\rsc" is the resource folder
     */
    static final String path = System.getProperty("user.dir");
    /**
     * A linked list containing all the asteroids and artificial satellites in the background
     */
    private LinkedList<Satellite> satellites;
    /**
     * The wallpaper in the background
     */
    private BufferedImage wallpaper;

    public Background() {
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void tick() {
        // Satellites
        if (random.nextInt(60) < 1)
            satellites.add(new Satellite());
        for (int i = 0; i < satellites.size(); i++) {
            Satellite sat = satellites.get(i);
            sat.setX(sat.getX() + sat.getXVelocity());
            if (sat.getX() > Window.WIDTH)
                satellites.remove(i);
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
        for (Satellite sat : satellites) {
            g.drawImage(sats[sat.getSatellite()], (int) (sat.getX() + sat.getXVelocity() * d),
                    (int) sat.getY(),
                    (int) sat.getDimension().getWidth(),
                    (int) sat.getDimension().getHeight(), null);
        }
    }

}
