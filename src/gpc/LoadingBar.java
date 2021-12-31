package src.gpc;

import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.Canvas;
import java.awt.FontMetrics;

public class LoadingBar extends Panel {

    private Color color1 = new Color(170, 161, 255), color2 = new Color(94, 57, 234);
    private Color[] colors = new Color[] {
        new Color(164, 242, 233)
    };
    private Font font;
    private int inc, bar;
    private int duration;
    private boolean done;

    public LoadingBar(Dimension size, Font font) {
        super(size);
        this.font = font;
        this.inc = new Random().nextInt(colors.length);
        this.bar = 0;
        this.duration = 60 * 3;
        this.done = false;
    }

    public LoadingBar(Dimension size, Font font, int duration) {
        super(size);
        this.font = font;
        this.inc = new Random().nextInt(colors.length);
        this.bar = 0;
        this.duration = duration;
        this.done = false;
    }

    public BufferedImage render() {
        return simRender(0.0);
    }

    public BufferedImage simRender(double d) {
        FontMetrics fm = new Canvas().getFontMetrics(font);
        int banner = fm.getHeight() + font.getSize()/2;
        BufferedImage img = new BufferedImage(dimension.width, dimension.height + banner, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color2);
        g.fillRect(0, banner, dimension.width, 2);
        g.fillRect(0, dimension.height-3+banner, dimension.width, 2);
        g.fillRect(0, banner, 2, dimension.height);
        g.fillRect(dimension.width-3, banner, 2, dimension.height);
        g.setPaint(new GradientPaint(0, dimension.height/2, color1, dimension.width, dimension.height/2, color2));
        g.fillRect(6, 6+banner, dimension.width-13, dimension.height-13);
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(((done && inc == duration) ? "Loading resources..." : "Download complete!").toUpperCase(), 0, g.getFontMetrics().getHeight());
        return img;
    }

    public void tick() {
        if (inc == duration) {
            this.inc = 0;
            this.bar = (bar+1)%colors.length;
        }
        if (inc < duration)
            this.inc++;
    }

    /**
     * Finishes the animation and returns true as soon as it is done
     * 
     * @return
     */
    public boolean finished() {
        if (!done)
            this.done = true;
        return this.done && ( this.inc % this.duration ) == 0;
    }

    /**
     * Attempts to retrieve the size of a file or image at a URL
     * 
     * @param url the URL at which the file is stored
     * @return the size of the file at the destination in bytes
     */
    public static final int getFileSize(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).disconnect();
            }
        }
    }

}