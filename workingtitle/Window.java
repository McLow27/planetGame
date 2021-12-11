package workingtitle;

import java.awt.Canvas;
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
        frame.add(engine);
        frame.setVisible(true);
        engine.start();
    }

}
