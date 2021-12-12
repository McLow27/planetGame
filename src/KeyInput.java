package src;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    Handler handler;

    public KeyInput(Handler handler) {
        this.handler = handler;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        // Do something with the input
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        // Do something with the input
    }

    public void keyTyped(KeyEvent e) {
        int key = e.getKeyCode();
        // Do something with the input
    }

}
