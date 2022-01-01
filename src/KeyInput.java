package src;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * A {@link java.awt.event.KeyAdapter} for receiving and handling keyboard input, 
 * this will execute the corresponding methods of the engine's current {@link GUI} object
 * 
 * @author TheCommandBlock
 * @since 12/12/2021
 */
public class KeyInput extends KeyAdapter {

    /**
     * The game engine's {@link Engine.UI} object to access the current {@link GUI}
     */
    private Engine.UI handler;

    /**
     * A new {@link java.awt.event.KeyAdapter} that can be added to the {@link Engine};
     * in the event of a keyboard input, this will call the corresponding methods 
     * of the {@link Engine.UI}'s current {@link GUI} object
     * 
     * @param handler The {@link Engine.UI} object of the game engine to forward the events
     */
    public KeyInput(Engine.UI handler) {
        this.handler = handler;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
        handler.getUI().keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        handler.getUI().keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        handler.getUI().keyTyped(e);
    }

}
