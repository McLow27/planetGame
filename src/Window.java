package src;

import javax.swing.JFrame;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.Image;
import java.awt.Dimension;

/**
 * A class for generating a {@link javax.swing.JFrame}, setting all its attributes and finally 
 * adding the {@link Engine} object, thus launching the game engine.
 * 
 * @author TheCommandBlock
 * @since 12/12/2021
 */
public class Window {

    /**
     * The dimensions of the {@link javax.swing.JFrame}
     */
    public static final int WIDTH = Engine.WIDTH, HEIGHT = Engine.WIDTH / 16 * 9;
    /**
     * The title of the {@link javax.swing.JFrame}
     */
    public static final String TITLE = Engine.TITLE;

    /**
     * The constructor will set up a new {@link javax.swing.JFrame} with an {@link Engine} object 
     * and finally launch that game engine.
     * 
     * @param width  the width of the frame (for customiseability those are required separately)
     * @param height the height of the frame ('')
     * @param title  the title of the frame ('')
     * @param engine a {@link Engine} object that will be added to this frame and launched
     */
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
