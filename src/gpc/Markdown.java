package src.gpc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.*;
import src.utl.Lambda.Bool;
import src.utl.Tuple.Pair;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;

public class Markdown extends Panel {

    private String[] markdown;
    private Font font;
    private float spacing = 2f;
    private Color color = Color.WHITE;
    private boolean fadein = true;
    
    public Markdown(Dimension dimension, Font font, String markdown) {
        super(dimension);
        this.font = font;
        this.markdown = markdown.split("\\R");
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
        this.markdown = md.split("\\R");
        System.out.println(this.markdown.length);
    }

    public BufferedImage render() {
        return simRender(0.0);
    }

    public BufferedImage simRender(double delta) {
        FontMetrics fm = new Canvas().getFontMetrics(font);
        int height = fm.getHeight();
        BufferedImage img = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        g.setFont(font);
        g.setColor(color);
        float ly = 0;
        Bool<Pair<String, String>> lambda = (tuple) -> {
            Pattern ptr = Pattern.compile(tuple.getAlpha());
            Matcher mtr = ptr.matcher(tuple.getBeta());
            return mtr.find();
        };
        for (int l = 0; l < markdown.length && ly < dimension.height; l++) {
            if (lambda.check(new Pair<String, String>("#{1,5} .+", markdown[l]))) {
                int lvl = 5 - markdown[l].substring(0, markdown[l].indexOf(' ')).length();
                g.setFont(font.deriveFont(Font.BOLD, font.getSize() + 2 * lvl));
            } else {
                g.setFont(font);
            }
            g.drawString(markdown[l], 0, (int) ly + height);
            ly += g.getFontMetrics().getHeight() + spacing;
        }
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

}
