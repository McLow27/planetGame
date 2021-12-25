package src.gpc;

import java.net.URL;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.*;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import src.utl.Lambda.Bool;
import src.utl.Tuple.Pair;

public class Markdown extends Panel {

    private String[] markdown;
    private Font font;
    private float spacing = 2f;
    private Color color = Color.WHITE;
    private Color code = new Color(11, 255, 131), highlight = new Color(255, 254, 6), link = new Color(25, 254, 255);
    private boolean fadein = true;
    private int scroll = 0;

    private HashMap<String, Image> imgs;
    private HashMap<String, Rectangle> links;
    
    public Markdown(Dimension dimension, Font font, String markdown) {
        super(dimension);
        this.font = font;
        this.markdown = this.format(markdown);
        this.imgs = loadImages(this.markdown);
        this.links = new HashMap<String, Rectangle>();
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
        this.markdown = this.format(md);
        this.imgs = loadImages(this.markdown);
        this.links = new HashMap<String, Rectangle>();
    }

    private HashMap<String, Image> loadImages(String[] lines) {
        HashMap<String, Image> images = new HashMap<String, Image>();
        for (String line : lines) {
            Matcher image = Pattern.compile("!\\[\\]\\((\\S+)(\\s\".+\")?\\)").matcher(line);
            if (image.find()) {
                try { 
                    URL url = new URL(image.group(1));
                    Image img = ImageIO.read(url);
                    images.put(image.group(1), img);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return images;
    }

    private String[] format(String md) {
        String[] lines = md.split("\\R");
        LinkedList<String> lns = new LinkedList<String>();
        FontMetrics fm = new Canvas().getFontMetrics(font);
        for (String line : lines) {
            Matcher image = Pattern.compile("!\\[\\]\\((\\S+)(\\s\".+\")?\\)").matcher(line);
            if (image.find())
                lns.add(line + "\n");
            else if (fm.stringWidth(line) > dimension.width) {
                String total = "";
                for (int i = 0; i < line.length(); i++) {
                    if (fm.stringWidth(total + (line.indexOf(" ", i) != -1 ? line.substring(i, line.indexOf(" ", i)) : "")) >= dimension.width) {
                        lns.add(total);
                        total = "";
                    }
                    total += line.charAt(i);
                }
                lns.add(total + "\n");
            } else
                lns.add(line + "\n");
        }
        String[] result = new String[lns.size()];
        for (int i = 0; i < lns.size(); i++)
            result[i] = lns.get(i);
        return result;
    }

    public BufferedImage render() {
        return simRender(0.0);
    }

    /**
     * Bodged markdown formatting and render method
     */
    public BufferedImage simRender(double delta) {
        BufferedImage img = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        g.setFont(font);
        g.setColor(color);
        float ly = -scroll;
        float lx = 0;
        // Lambda function to check RegEx
        final Bool<Pair<String, String>> lambda = (tuple) -> {
            Pattern ptr = Pattern.compile(tuple.getAlpha());
            Matcher mtr = ptr.matcher(tuple.getBeta());
            return mtr.find();
        };
        HashMap<String, Rectangle> links = new HashMap<String, Rectangle>();
        // Iterate through the lines of the markdown
        for (int l = 0; l < markdown.length && ly < dimension.height; l++) {
            Font fn = font;
            lx = 0;
            String line = markdown[l];
            // No idea how this could even happen
            if (line.length() == 0)
                continue;
            Matcher matcher = Pattern.compile("!\\[\\]\\((\\S+)(\\s\".+\")?\\)").matcher(line);
            if (matcher.find()) {
                // Rendering images
                String id = matcher.group(1); 
                if (imgs.get(id).getWidth(null) > dimension.width) {
                    // For too large images
                    if (ly + imgs.get(id).getHeight(null) * dimension.width / imgs.get(id).getWidth(null) > 0)
                        g.drawImage(imgs.get(id), 0, (int) ly, dimension.width, imgs.get(id).getHeight(null) * dimension.width / imgs.get(id).getWidth(null), null);
                    ly += imgs.get(id).getHeight(null) * dimension.width / imgs.get(id).getWidth(null);
                } else {
                    // For fitting images
                    if (ly + imgs.get(id).getHeight(null) > 0)
                        g.drawImage(imgs.get(id), 0, (int) ly, null);
                    ly += imgs.get(id).getHeight(null);
                }
                continue;
            } else if (lambda.check(new Pair<String, String>("#{1,5} .+", line))) {
                // Headers
                String[] parts = line.split("\\s", 2);
                fn = font.deriveFont(Font.PLAIN, (float) (font.getSize() * (2.2 - parts[0].length() / 5.0)));
                line = parts[1];
            } else if (lambda.check(new Pair<String, String>("^---$", line))) {
                // Horizontal rule
                int h = 2;
                g.fillRect(0, (int) (ly + (g.getFontMetrics().getHeight() - h) / 2), dimension.width, h);
                ly += g.getFontMetrics().getHeight() + spacing;
                continue;
            } else if (lambda.check(new Pair<String, String>("^\\d\\. \\w+", line))) {
                // Ordered list
                lx = 6;
            } else if (lambda.check(new Pair<String, String>("\\|(.+?\\|)+", line)) && lambda.check(new Pair<String, String>("\\|(\s?-+\s?\\|)+", markdown[l+1]))) {
                int rows = line.split("\\|").length;
                LinkedList<String[]> table = new LinkedList<String[]>();
                l+=2;
                while(l < markdown.length && ly < dimension.height && lambda.check(new Pair<String, String>("\\|(.+?\\|)+", line))) {
                    String[] column = markdown[l].split("\\|");
                }
                int[] max = new int[table.get(0).length];
                for (int c = 0; c < table.size(); c++) {
                    for (int r = 0; r < max.length; r++) {
                        if (max[r] < table.get(c)[r].strip().length())
                            max[r] = table.get(c)[r].strip().length();
                    }
                }
            }
            g.setFont(fn);
            if (ly + g.getFontMetrics().getHeight() < 0) {
                // To increase efficiency, lines that are not currently being shown are not rendered
                ly += g.getFontMetrics().getHeight() + spacing;
                continue;
            }
            // Rendering the line
            for (int c = 0; c < line.length(); c++) {
                String r = line.substring(c);
                char h = line.charAt(c);
                Font f = g.getFont();
                int height = g.getFontMetrics().getHeight();
                if (h == '\n') {
                    // At the end of the line
                    ly += spacing;
                    break;
                } else if (lambda.check(new Pair<String, String>("^-|\\* \\w+", line)) && c == 0) {
                    // Unordered list
                    lx = 6;
                    int rd = 5;
                    g.fillOval((int) lx, (int) ly + rd + height / 2, rd, rd);
                    lx += rd;
                    continue;
                } else if (lambda.check(new Pair<String, String>("^[\\*_]{2}", r))) {
                    // Bold
                    g.setFont(f.deriveFont(f.getStyle() == Font.BOLD ? Font.PLAIN : Font.BOLD));
                    ++c;
                    continue;
                } else if (lambda.check(new Pair<String, String>("^[\\*_]{1}", r))) {
                    // Italic
                    g.setFont(f.deriveFont(f.getStyle() == Font.ITALIC ? Font.PLAIN : Font.ITALIC));
                    continue;
                } else if (lambda.check(new Pair<String, String>("^\\[(.+)\\]\\(((https:\\/\\/)?(\\w+\\.)?.+\\.\\w+\\/?)( \".+\")?\\)", r))) {
                    // Links
                    Matcher m = Pattern.compile("^\\[(.+)\\]\\(((https:\\/\\/)?(\\w+\\.)?.+\\.\\w+\\/?)( \".+\")?\\)").matcher(r);
                    if (m.find())
                        links.put(m.group(2), new Rectangle((int) lx, (int) ly, g.getFontMetrics().stringWidth(m.group(1)), height));
                    g.setColor(this.link);
                    continue;
                } else if (lambda.check(new Pair<String, String>("^\\]\\((https:\\/\\/)?(\\w+\\.)?.+\\.\\w+\\/?( \".+\")?\\)", r))) {
                    // End of link
                    Matcher m = Pattern.compile("^\\]\\((https:\\/\\/)?(\\w+\\.)?.+\\.\\w+\\/?( \".+\")?\\)").matcher(r);
                    if (m.find()) {
                        c += m.group(0).length() - 1;
                    }
                    g.setColor(this.color);
                    continue;
                } else if (r.charAt(0) == '`') {
                    // Code sections
                    g.setColor(this.code);
                    do {
                        h = line.charAt(++c);
                        if (h == '`') continue;
                        // Draw the character without formatting
                        g.drawString(Character.toString(h), (int) lx, (int) ly + height);
                        lx += g.getFontMetrics().stringWidth(Character.toString(h));
                        // Jump to next line if necessary
                        if (c == line.length()) {
                            lx = 0;
                            c = 0;
                            ly += g.getFontMetrics().getHeight();
                            l++;
                        }
                    } while (h != '`');
                    g.setColor(this.color);
                    continue;
                } else if (lambda.check(new Pair<String, String>("^==.*?==", r))) {
                    // Highlight
                    g.setColor(g.getColor() == this.highlight ? this.color : this.highlight);
                }
                // Draw the character
                g.drawString(Character.toString(h), (int) lx, (int) ly + height);
                lx += g.getFontMetrics().stringWidth(Character.toString(h));
            }
            // Skip to the next line
            ly += g.getFontMetrics().getHeight();
        }
        // Refresh the links
        this.links = links;
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
        return this.links;
    }

}
