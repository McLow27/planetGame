package src.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.LinkedList;
import java.awt.Dimension;
import java.util.Random;
import src.Engine;
import src.Window;
import src.GUI;

public class Title extends GUI {

    static final String path = System.getProperty("user.dir");
    private static BufferedImage[] sats;
    private Random random = new Random();

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

            // double s = random.nextDouble() * (max - min) / width + min / width;
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

    BufferedImage wallpaper;
    LinkedList<Satellite> satellites;

    public Title() {
        try {
            wallpaper = ImageIO.read(new File(path + "\\rsc\\title.png"));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void render(Graphics g) {
        int w = wallpaper.getWidth(), h = wallpaper.getHeight();
        if (w / Engine.WIDTH < h / Engine.HEIGHT) {
            h = h * Engine.WIDTH / w;
            w = Engine.WIDTH;
        } else {
            w = w * Engine.HEIGHT / h;
            h = Engine.HEIGHT;
        }
        g.drawImage(wallpaper, (Engine.WIDTH - w) / 2, (Engine.HEIGHT - h) / 2, w, h, null);

        simRender(g, 0.0);
    }

    public void simRender(Graphics g, double d) {
        int w = wallpaper.getWidth(), h = wallpaper.getHeight();
        if (w / Window.WIDTH < h / Window.HEIGHT) {
            h = h * Window.WIDTH / w;
            w = Window.WIDTH;
        } else {
            w = w * Window.HEIGHT / h;
            h = Window.HEIGHT;
        }
        g.drawImage(wallpaper, (Window.WIDTH - w) / 2, (Window.HEIGHT - h) / 2, w, h, null);

        // TODO find out how to rotate the satellite
        for (Satellite sat : satellites) {
            g.drawImage(sats[sat.getSatellite()], (int) (sat.getX() + sat.getXVelocity() * d),
                    (int) sat.getY(),
                    (int) sat.getDimension().getWidth(),
                    (int) sat.getDimension().getHeight(), null);
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
