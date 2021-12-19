package src;

import javax.swing.JFrame;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Canvas;

public class Window extends Canvas {

    public static final int WIDTH = Engine.WIDTH, HEIGHT = Engine.WIDTH / 16 * 9;
    public static final String TITLE = Engine.TITLE;
    private JFrame frame;

    public Window(int width, int height, String title, Engine engine) {
        frame = new JFrame(title);
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

    public JFrame getFrame() {
        return frame;
    }

}
