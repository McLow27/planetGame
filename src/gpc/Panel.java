package src.gpc;

import java.awt.image.BufferedImage;
import java.awt.Dimension;

/**
 * An abstract class for all graphics panels that render onto a {@link java.awt.image.BufferedImage}.
 * 
 * @author TheCommandBlock
 * @since 15/12/2021
 */
public abstract class Panel {

    /**
     * The dimensions of the panel
     */
    protected Dimension dimension;

    /**
     * The super constructor of the panel always requiring the dimensions
     * 
     * @param dimension an object defining the width and height of the panel
     */
    public Panel(Dimension dimension) {
        this.dimension = dimension;
    }

    /**
     * Ticks the panel
     */
    public void tick() {
    }

    /**
     * Renders the panel onto a {@link java.awt.image.BufferedImage} of its dimensions
     * 
     * @return an image that can be drawn onto the game canvas
     */
    public abstract BufferedImage render();

    /**
     * Render the panel except with intermediate steps in-between ticks. By default this 
     * method simply executes the render method.
     * 
     * @param delta a floating point value between 0.0 and 1.0 of the time that 
     *              has passed between last and next tick
     * @return an image that can be drawn onto the game canvas
     */
    public BufferedImage render(double delta) {
        return render();
    }

    /**
     * Gets the width of this panel
     * 
     * @return the width dimension
     */
    public int getWidth() {
        return dimension.width;
    }

    /**
     * Gets the height of this panel
     * 
     * @return the height dimension
     */
    public int getHeight() {
        return dimension.height;
    }

}
