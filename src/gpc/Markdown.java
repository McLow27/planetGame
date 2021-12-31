package src.gpc;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.LinkedList;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Markdown extends Panel {

    private Syntax[] markdown;
    private boolean fadein = true;
    private int scroll = 0;

    private static String[] split(String str, char rgx) {
        LinkedList<String> list = new LinkedList<String>();
        String buffer = "";
        for (char c : str.toCharArray()) {
            if (c == rgx) {
                list.add(buffer);
                buffer = "";
                continue;
            }
            buffer += c;
        }
        String[] result = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
            result[i] = list.get(i);
        return result;
    }

    public Markdown(Dimension dimension, Font font, String markdown) {
        super(dimension);
        Syntax.font = font;
        Syntax.width = dimension.width;
        this.markdown = Syntax.compile(Markdown.split(markdown, '\n'));
    }

    public Markdown(Dimension dimension, Font font, File markdown) throws FileNotFoundException {
        super(dimension);
        Syntax.font = font;
        Syntax.width = dimension.width;
        Scanner scan = new Scanner(markdown);
        String md = "";
        while (scan.hasNext()) {
            md += scan.nextLine() + "\n";
        };
        scan.close();
        this.markdown = Syntax.compile(Markdown.split(md, '\n'));
    }

    public BufferedImage render() {
        return simRender(0.0);
    }

    /**
     * No longer bodged markdown formatting and render method
     */
    public BufferedImage simRender(double delta) {
        BufferedImage img = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        int y = -scroll;
        for (Syntax element : markdown) {
            if (!(y + element.getHeight() < 0 || y > dimension.height))
                element.render(g, y);
            y += element.getHeight() + Syntax.spacing;
        }
        return img;
    }

    /**
     * Set the spacing between lines
     * 
     * @param spacing a float value denoting the spacing
     */
    public void setSpacing(int spacing) {
        Syntax.spacing = spacing;
    }

    /**
     * Get the spacing between lines
     * 
     * @return a float value denoting the spacing
     */
    public float getSpacing() {
        return Syntax.spacing;
    }

    /**
     * Set the font color
     * 
     * @param color the new color of the font
     */
    public void setColor(Color color) {
        Syntax.color = color;
    }

    /**
     * Get the current font color
     * 
     * @return the color of the font
     */
    public Color getColor() {
        return Syntax.color;
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
        int y = 0;
        for (Syntax element : markdown)
            y += element.getHeight() + Syntax.spacing;
        if (y - dimension.height + 60 < this.scroll)
            this.scroll = y - dimension.height + 60;
    }

    /**
     * Set the scroll amount to a value between zero and the end
     * 
     * @param value the total scroll value
     */
    public void setScroll(int value) {
        if (value >= 0)
            this.scroll = value;
        else {
            int y = 0;
            for (Syntax element : markdown)
                y += element.getHeight() + Syntax.spacing;
            if (y - dimension.height + 60 < this.scroll)
                this.scroll = y - dimension.height + 60;
            else
                this.scroll = 0;
        }
    }

    /**
     * Sets the color that highlighted words will have
     * 
     * @param highlight the color of highlighted sections
     */
    public void setHighlight(Color highlight) {
        Syntax.highlight = highlight;
    }

    /**
     * Get the color that highlighted words will have
     * 
     * @return the color of highlighted sections
     */
    public Color getHighlight() {
        return Syntax.highlight;
    }

    /**
     * Sets the color that links will have
     * 
     * @param link the color of links
     */
    public void setLink(Color link) {
        Syntax.link = link;
    }

    /**
     * Get the color that links will have
     * 
     * @return the color of links
     */
    public Color getLink() {
        return Syntax.link;
    }

    /**
     * Sets the color that code sections will have
     * 
     * @param code the color of code sections
     */
    public void setCode(Color code) {
        Syntax.code = code;
    }

    /**
     * Get the color that code sections will have
     * 
     * @return the color of code sections
     */
    public Color getCode() {
        return Syntax.code;
    }

    /**
     * Get the coordinates of sections with a link
     * 
     * @return a hashmap with links and their actionbox rectangles in the canvas
     */
    public HashMap<URL, Rectangle> getLinks() {
        HashMap<URL, Rectangle> map = new HashMap<URL, Rectangle>();
        Syntax.Link[] array = Syntax.getLinks();
        for (Syntax.Link link : array) {
            Rectangle box = link.getActionBox();
            map.put(link.getURL(), new Rectangle(box.x, box.y - scroll, box.width, box.height));
        }
        return map;
    }

}
