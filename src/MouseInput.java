package src;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * A {@link java.awt.event.MouseAdapter} for receiving and handling mouse input, 
 * this will execute the corresponding methods of the engine's current {@link GUI} object
 * 
 * @author TheCommandBlock
 * @since 12/12/2021
 */
public class MouseInput extends MouseAdapter {

    /**
     * The game engine's {@link Engine.UI} object to access the current {@link GUI}
     */
    private Engine.UI handler;

    /**
     * A new {@link java.awt.event.MouseAdapter} that can be added to the {@link Engine};
     * in the event of any mouse input, this will call the corresponding methods 
     * of the {@link Engine.UI}'s current {@link GUI} object
     * 
     * @param handler The {@link Engine.UI} object of the game engine to forward the events
     */
    public MouseInput(Engine.UI handler) {
        this.handler = handler;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.handler.getUI().mouseClicked(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.handler.getUI().mouseDragged(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.handler.getUI().mouseEntered(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.handler.getUI().mouseExited(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.handler.getUI().mouseMoved(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.handler.getUI().mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.handler.getUI().mouseReleased(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.handler.getUI().mouseWheelMoved(e);
    }

}
