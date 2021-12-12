package src;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A key adapter for receiving and handling keyboard input, this will execute
 * the corresponding methods of the engine's current GUI object
 */
public class KeyInput extends KeyAdapter {

    private Engine.UI handler;

    /**
     * A new key adapter that can be added to the frame; in the event of a keyboard
     * input, this will call the corresponding methods of the UI's current
     * GUI object
     */
    public KeyInput(Engine.UI handler) {
        this.handler = handler;
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
        handler.getUI().keyPressed(e);
    }

    public void keyReleased(KeyEvent e) {
        handler.getUI().keyReleased(e);
    }

    public void keyTyped(KeyEvent e) {
        handler.getUI().keyTyped(e);
    }

}
