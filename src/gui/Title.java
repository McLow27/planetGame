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
        private double velX, velR;
        private Dimension dimension;
        private int sat;

        public Satellite() {
            this.velX = (random.nextInt(5) + 1) / 4.0;
            this.velR = random.nextInt(7) - 3;
            this.sat = random.nextInt(sats.length);
            final int w = sats[this.sat].getWidth(), h = sats[this.sat].getHeight();
            final int maxS = 120, minS = 60;
            double s;
            do
                s = random.nextDouble();
            while (s * w < maxS && s * w > minS);
            this.dimension = new Dimension((int) (w * s), (int) (h * s));
            this.x = -sats[this.sat].getWidth();
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

        public double getRVelocity() {
            return velR;
        }

        public Dimension getDimension() {
            return dimension;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setRotation(double r) {
            this.r = r;
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
            int r = random.nextInt(12) + 12;
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

        for (Satellite sat : satellites) {
            g.drawImage(sats[sat.getSatellite()], (int) (sat.getX() + sat.getXVelocity() * d),
                    (int) sat.getY(),
                    (int) sat.getDimension().getWidth(),
                    (int) sat.getDimension().getHeight(), null);
        }
    }

    public void tick() {
        if (random.nextInt(120) < 1)
            satellites.add(new Satellite());

        for (int i = 0; i < satellites.size(); i++) {
            Satellite sat = satellites.get(i);
            sat.setX(sat.getX() + sat.getXVelocity());
            // sat.setRotation(sat.getRotation() + sat.getRVelocity());
            if (sat.getX() > Window.WIDTH)
                satellites.remove(i);
        }
    }
}
