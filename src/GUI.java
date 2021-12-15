package src;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * A graphics user interface that can be ticked and rendered by the engine
 */
public abstract class GUI {

    /**
     * A method for rendering all objects of the GUI
     * 
     * @param g the graphics object of the engine's canvas
     */
    protected abstract void render(Graphics g);

    /**
     * A method for rendering all objects of the GUI but simulating a smoother
     * movement for renders in between ticks
     * 
     * @param g the graphics object of the engine's canvas
     * @param d the delta time can be anything between 0 and 1
     */
    protected void simRender(Graphics g, double d) {
    }

    /**
     * A tick method to refresh the objects on screen; this method is not necessary
     * but will be executed at a constant 20 ticks per second
     */
    protected void tick() {
    }

    /**
     * Called when a key is pressed
     * 
     * @param e the key event
     */
    protected void keyPressed(KeyEvent e) {
    }

    /**
     * Called when a key is released
     * 
     * @param e the key event
     */
    protected void keyReleased(KeyEvent e) {
    }

    /**
     * Called when a key is typed
     * 
     * @param e the key event
     */
    protected void keyTyped(KeyEvent e) {
    }

    /**
     * Called when the mouse is pressed and released
     * 
     * @param e the mouse event
     */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Called when the mouse is dragged
     * 
     * @param e the mouse event
     */
    public void mouseDragged(MouseEvent e) {
    }

    /**
     * Called when the mouse enters an object
     * 
     * @param e the mouse event
     */
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Called when the mouse exits an object
     * 
     * @param e the mouse event
     */
    public void mouseExited(MouseEvent e) {
    }

    /**
     * Called when the mouse is moved
     * 
     * @param e the mouse event
     */
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * Called when the mouse is pressed
     * 
     * @param e the mouse event
     */
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Called when the mouse is released
     * 
     * @param e the mouse event
     */
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * Called when the mousewheel is rotated
     * 
     * @param e the mousewheel event
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

}
