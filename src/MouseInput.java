package src;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * A mouse adapter for receiving and handling mouse input, this will execute
 * the corresponding methods of the engine's current GUI object
 */
public class MouseInput extends MouseAdapter {

    private Engine.UI handler;

    public MouseInput(Engine.UI handler) {
        this.handler = handler;
    }

    public void mouseClicked(MouseEvent e) {
        this.handler.getUI().mouseClicked(e);
    }

    public void mouseDragged(MouseEvent e) {
        this.handler.getUI().mouseDragged(e);
    }

    public void mouseEntered(MouseEvent e) {
        this.handler.getUI().mouseEntered(e);
    }

    public void mouseExited(MouseEvent e) {
        this.handler.getUI().mouseExited(e);
    }

    public void mouseMoved(MouseEvent e) {
        this.handler.getUI().mouseMoved(e);
    }

    public void mousePressed(MouseEvent e) {
        this.handler.getUI().mousePressed(e);
    }

    public void mouseReleased(MouseEvent e) {
        this.handler.getUI().mouseReleased(e);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        this.handler.getUI().mouseWheelMoved(e);
    }

}
