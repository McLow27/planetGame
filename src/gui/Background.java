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

/**
 * An animated background with random Kurzgesagt-style satellites moving over the screen.
 * 
 * @author TheCommandBlock
 * @since 14/12/2021
 */
public class Background extends GUI {

    /**
     * A class for the animated satellite objects on the background
     */
    static class Satellite {
        /**
         * Nothing but a random number generator
         */
        private static final Random random = new Random();
        /**
         * The coordinates of the satellite
         */
        private double x, y;
        /**
         * The velocity of the satellite along the x-axis in pixels per tick
         */
        private double velX;
        /**
         * The dimensions of the satellite
         */
        private Dimension dimension;
        /**
         * The index of the {@link java.awt.image.BufferedImage} of the satellite in the array
         */
        private int sat;

        /**
         * Creates a new satellite object and randomly sets all the attributes
         */
        public Satellite() {
            this.velX = (random.nextInt(50) + 10) / 4.0;
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

        /**
         * Gets the x-coordinate of the satellite
         * 
         * @return an integer between the negative width of this satellite 
         * and the width of the screen
         */
        public double getX() {
            return x;
        }

        /**
         * Gets the y-coordinate of the satellite
         * 
         * @return an integer between zero and the height of the screen
         */
        public double getY() {
            return y;
        }

        /**
         * Gets the index of the image of the satellite in the array
         * 
         * @return an integer within the bounds of the array
         */
        public int getSatellite() {
            return sat;
        }

        /**
         * Gets the x-velocity of the satellite in pixels per tick
         * 
         * @return the x-velocity of the satellite somewhere between 1.2 and 5
         */
        public double getXVelocity() {
            return velX;
        }

        /**
         * Gets the dimensions of the image of this satellite
         * 
         * @return a dimension object to which the image of this satellite should be cropped
         */
        public Dimension getDimension() {
            return dimension;
        }

        /**
         * Updates the x-coordinate of this satellite
         * 
         * @param x the new x-coordinate
         */
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
     * The path of this executable, + "\\rsc" is the resource folder
     */
    static final String path = System.getProperty("user.dir");
    /**
     * A list containing all the asteroids and artificial satellites in the background
     */
    private LinkedList<Satellite> satellites;
    /**
     * The wallpaper in the background
     */
    private BufferedImage wallpaper;

    /**
     * Creates a new background to use for the title screen, retrieves all resources 
     * and randomly generates an army of satellites that float over the screen.
     */
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

    @Override
    public void tick() {
        // Satellites
        if (random.nextInt(20) < 1)
            satellites.add(new Satellite());
        for (int i = 0; i < satellites.size(); i++) {
            Satellite sat = satellites.get(i);
            sat.setX(sat.getX() + sat.getXVelocity());
            if (sat.getX() > Window.WIDTH)
                satellites.remove(i);
        }
    }

    @Override
    public void render(Graphics g) {
        render(g, 0.0);
    }

    @Override
    public void render(Graphics g, double d) {
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
                    (int) sat.getY(), (int) sat.getDimension().getWidth(),
                    (int) sat.getDimension().getHeight(), null);
        }
    }

}
