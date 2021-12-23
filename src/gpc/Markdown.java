package src.gpc;

import java.net.URL;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
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
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Markdown extends Panel {

    private String[] markdown;
    private Font font;
    private float spacing = 2f;
    private Color color = Color.WHITE;
    private boolean fadein = true;

    private HashMap<String, Image> imgs;
    
    public Markdown(Dimension dimension, Font font, String markdown) {
        super(dimension);
        this.font = font;
        this.markdown = markdown.split("\\R");
        this.imgs = loadImages(this.markdown);
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
        this.imgs = loadImages(this.markdown);
    }

    private HashMap<String, Image> loadImages(String[] lines) {
        HashMap<String, Image> images = new HashMap<String, Image>();
        for (String line : this.markdown) {
            Matcher image = Pattern.compile("!\\[\\]\\((\\S+)(\\s\".+\")?\\)").matcher(line);
            if (image.find()) {
                try { 
                    URL url = new URL(image.group(1));
                    Image img = ImageIO.read(url);
                    images.put(image.group(1), img);
                } catch (IOException e) {
                    // handle IOException
                }
            }
        }
        return images;
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
        float lx = 0;  
        final Bool<Pair<String, String>> lambda = (tuple) -> {
            Pattern ptr = Pattern.compile(tuple.getAlpha());
            Matcher mtr = ptr.matcher(tuple.getBeta());
            return mtr.find();
        };
        for (int l = 0; l < markdown.length && ly < dimension.height; l++) {
            Font fn = font;
            String line = markdown[l];
            if (line.length() == 0)
                continue;
            if (lambda.check(new Pair<String, String>("#{1,5} .+", line))) {
                String[] parts = line.split("\\s", 2);
                fn = font.deriveFont(Font.PLAIN, (float) (font.getSize() * (2.2 - parts[0].length() / 5.0)));
                line = parts[1];
            }
            g.setFont(fn);
            for (int c = 0; c < line.length(); c++) {
                String r = line.substring(c), h = Character.toString(line.charAt(c));
                Font f = g.getFont();
                Matcher matcher = Pattern.compile("!\\[\\]\\((\\S+)(\\s\".+\")?\\)").matcher(line);
                if (c == 0 && matcher.find()) {
                    String id = matcher.group(1);
                    g.drawImage(imgs.get(id), 0, (int) ly, null);
                    ly += imgs.get(id).getHeight(null);
                    lx = 0;
                    break;
                } else if (lambda.check(new Pair<String, String>("^[\\*_]{2}", r))) {
                    g.setFont(f.deriveFont(f.getStyle() == Font.BOLD ? Font.PLAIN : Font.BOLD));
                    ++c;
                    continue;
                } else if (lambda.check(new Pair<String, String>("^[\\*_]{1}", r))) {
                    g.setFont(f.deriveFont(f.getStyle() == Font.ITALIC ? Font.PLAIN : Font.ITALIC));
                    continue;
                }

                if (lx + g.getFontMetrics().stringWidth(r.indexOf(" ") != -1 ? r.substring(0, r.indexOf(" ")) : h) > dimension.width) {
                    ly += g.getFontMetrics().getHeight();
                    lx = 0;
                }
                g.drawString(h, (int) lx, (int) ly + height);
                lx += g.getFontMetrics().stringWidth(h);
            }
            ly += g.getFontMetrics().getHeight() + spacing;
            lx = 0;
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
