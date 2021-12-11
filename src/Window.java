package src;

import java.awt.Canvas;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import javax.swing.JFrame;
import java.awt.Dimension;

public class Window extends Canvas {

    public Window(int width, int height, String title, Engine engine) {
        JFrame frame = new JFrame(title);
        frame.setPreferredSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));
        frame.setMaximumSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        try {
            Image icon = ImageIO.read(new File(System.getProperty("user.dir") + "\\rsc\\icon.png"));
            frame.setIconImage(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        frame.add(engine);
        frame.setVisible(true);
        engine.start();
    }

}
