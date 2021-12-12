package src.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import src.Engine;
import src.GUI;

public class Title extends GUI {

    BufferedImage wallpaper;

    public Title() {
        try {
            wallpaper = ImageIO.read(new File(System.getProperty("user.dir") + "\\rsc\\wallpaper.jpg"));
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
    }

}
