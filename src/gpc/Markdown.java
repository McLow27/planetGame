package src.gpc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

public class Markdown extends Panel {

    private String markdown;
    private Font font;
    private float spacing = 2f;
    private Color color = Color.WHITE;
    
    public Markdown(Dimension dimension, Font font, String markdown) {
        super(dimension);
        this.font = font;
        this.markdown = markdown;
    }

    public Markdown(Dimension dimension, Font font, File markdown) throws FileNotFoundException {
        super(dimension);
        this.font = font;
        Scanner scan = new Scanner(markdown);
        this.markdown = "";
        while (scan.hasNext()) {
            this.markdown += scan.nextLine();
        };
        scan.close();
    }

    public BufferedImage render() {
        return simRender(0.0);
    }

    public BufferedImage simRender(double delta) {
        FontMetrics fm = new Canvas().getFontMetrics(font);
        int height = fm.getHeight();
        BufferedImage img = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        g.setFont(font);
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

}
