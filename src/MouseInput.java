package src;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class MouseInput extends MouseAdapter {

    Handler handler;

    public MouseInput(Handler handler) {
        this.handler = handler;
    }

    public void mouseClicked(MouseEvent e) {
        int clicks = e.getClickCount();
        int x = e.getX(), y = e.getY();
        int button = e.getButton();
        // Do something with the input
    }

    public void mouseDragged(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        int button = e.getButton();
        // Do something with the input
    }

    public void mouseEntered(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        int button = e.getButton();
        // Do something with the input
    }

    public void mouseExited(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        int button = e.getButton();
        // Do something with the input
    }

    public void mouseMoved(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        int button = e.getButton();
        // Do something with the input
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        int button = e.getButton();
        // Do something with the input
    }

    public void mouseReleased(MouseEvent e) {
        int x = e.getX(), y = e.getY();
        int button = e.getButton();
        // Do something with the input
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        int clicks = e.getWheelRotation();
        // Do something with the input
    }

}
