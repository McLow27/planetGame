package src.gpc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Markdown extends Panel {

    private Syntax[] markdown;
    private Font font;
    private float spacing = 2f;
    private Color color = Color.WHITE;
    private Color code = new Color(11, 255, 131), highlight = new Color(255, 254, 6), link = new Color(25, 254, 255);
    private boolean fadein = true;
    private int scroll = 0;
    
    public Markdown(Dimension dimension, Font font, String markdown) {
        super(dimension);
        this.font = font;
        this.markdown = Syntax.compile(markdown.split("\\R"));
    }

    public Markdown(Dimension dimension, Font font, File markdown) throws FileNotFoundException {
        super(dimension);
        this.font = font;
        Scanner scan = new Scanner(markdown);
        String md = "";
        while (scan.hasNext()) {
            md += scan.nextLine() + "\n";
        };
        scan.close();
        this.markdown = Syntax.compile(md.split("\\R"));
    }

    public BufferedImage render() {
        return simRender(0.0);
    }

    /**
     * No longer bodged markdown formatting and render method
     */
    public BufferedImage simRender(double delta) {
        BufferedImage img = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        return img;
    }

    /**
     * Set the spacing between lines
     * 
     * @param spacing a float value denoting the spacing
     */
    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }

    /**
     * Get the spacing between lines
     * 
     * @return a float value denoting the spacing
     */
    public float getSpacing() {
        return this.spacing;
    }

    /**
     * Set the font color
     * 
     * @param color the new color of the font
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get the current font color
     * 
     * @return the color of the font
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Enables the fade-in animation
     */
    public void enableFadein() {
        if (!fadein)
            fadein = true;
    }

    /**
     * Disables the fade-in animation
     */
    public void disableFadein() {
        if (fadein)
            fadein = false;
    }

    /**
     * Returns whether the fade-in animation is acivated
     * 
     * @return if fade-in is enabled
     */
    public boolean isFadein() {
        return fadein;
    }

    /**
     * Scroll by any value, positive means down, negative means up
     * 
     * @param value the signed value by which to scroll
     */
    public void scroll(int value) {
        this.scroll += value;
        if (this.scroll < 0)
            this.scroll = 0;
    }

    /**
     * Set the scroll amount to a value between zero and the end
     * 
     * @param value the total scroll value
     */
    public void setScroll(int value) {
        if (value >= 0)
            this.scroll = value;
        else
            this.scroll = 0;
    }

    /**
     * Sets the color that highlighted words will have
     * 
     * @param highlight the color of highlighted sections
     */
    public void setHighlight(Color highlight) {
        this.highlight = highlight;
    }

    /**
     * Get the color that highlighted words will have
     * 
     * @return the color of highlighted sections
     */
    public Color getHighlight() {
        return this.highlight;
    }

    /**
     * Sets the color that links will have
     * 
     * @param link the color of links
     */
    public void setLink(Color link) {
        this.link = link;
    }

    /**
     * Get the color that links will have
     * 
     * @return the color of links
     */
    public Color getLink() {
        return this.link;
    }

    /**
     * Sets the color that code sections will have
     * 
     * @param code the color of code sections
     */
    public void setCode(Color code) {
        this.code = code;
    }

    /**
     * Get the color that code sections will have
     * 
     * @return the color of code sections
     */
    public Color getCode() {
        return this.code;
    }

    /**
     * Get the coordinates of sections with a link
     * 
     * @return a hashmap with links and their actionbox rectangles in the canvas
     */
    public HashMap<String, Rectangle> getLinks() {
        return new HashMap<String, Rectangle>();
    }

}
