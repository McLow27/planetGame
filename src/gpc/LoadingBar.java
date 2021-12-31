package src.gpc;

import java.net.URL;
import java.net.URLConnection;
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

    private Color color1 = new Color(170, 161, 255), color2 = new Color(94, 57, 234), color3 = new Color(164, 242, 233);
    private Font font;
    private int inc, dur;
    private boolean done;
    private String title = "Loading resources...", ready = "Download complete!";
    private Dimension bar;

    public LoadingBar(Dimension size, Font font) {
        super(new Dimension((int) size.getWidth(), (int) size.getHeight()
        + new Canvas().getFontMetrics(font).getHeight() + font.getSize()/2));
        this.font = font;
        this.inc = 1;
        this.dur = 60 * 3;
        this.done = false;
        this.bar = size;
    }

    public LoadingBar(Dimension size, Font font, int duration) {
        super(new Dimension((int) size.getWidth(), (int) size.getHeight()
        + new Canvas().getFontMetrics(font).getHeight() + font.getSize()/2));
        this.font = font;
        this.inc = 1;
        this.dur = duration;
        this.done = false;
        this.bar = size;
    }

    public LoadingBar(Dimension size, Font font, int duration, String[] title) {
        super(new Dimension((int) size.getWidth(), (int) size.getHeight()
        + new Canvas().getFontMetrics(font).getHeight() + font.getSize()/2));
        this.font = font;
        this.inc = 1;
        this.dur = duration;
        this.done = false;
        if (title.length >= 1)
            this.title = title[0];
        if (title.length >= 2)
            this.ready = title[1];
            this.bar = size;
    }

    public LoadingBar(Dimension size, Font font, int duration, String title) {
        super(new Dimension((int) size.getWidth(), (int) size.getHeight()
        + new Canvas().getFontMetrics(font).getHeight() + font.getSize()/2));
        this.font = font;
        this.inc = 1;
        this.dur = duration;
        this.done = false;
        this.title = title;
        this.ready = title;
        this.bar = size;
    }

    public BufferedImage render() {
        return simRender(0.0);
    }

    public BufferedImage simRender(double d) {
        FontMetrics fm = new Canvas().getFontMetrics(font);
        int banner = fm.getHeight() + font.getSize()/2;
        BufferedImage img = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(color2);
        g.fillRect(0, banner, bar.width, 2);
        g.fillRect(0, bar.height-3+banner, bar.width, 2);
        g.fillRect(0, banner, 2, bar.height);
        g.fillRect(bar.width-3, banner, 2, bar.height);
        g.setPaint(new GradientPaint(0, bar.height/2, color1, bar.width, bar.height/2, color2));
        g.fillRect(6, 6+banner, bar.width-13, bar.height-13);
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString((!done ? title : ready).toUpperCase(), 0, g.getFontMetrics().getHeight());
        if (inc > dur) {
            g.setColor(color3);
            g.fillRect(6, 6+banner, bar.width-13, bar.height-13);
        }
        int p = (bar.width-9) * (inc % dur) / dur;
        double rate = (bar.width-9) / (double) dur;
        if (p > 4) {
            g.setColor(color3);
            g.fillRect(6, 6+banner, (int) (p-4 + rate*d), bar.height-13);
        }
        int w = 4;
        if (p < 4) w = p;
        else if (p > bar.width+4) w = p - (bar.width + 4);
        g.setColor(Color.WHITE);
        g.fillRect((int) (p + rate*d), 6+banner, w, bar.height-13);
        return img;
    }

    public void tick() {
        if (!(done && (inc % dur) == 0)) this.inc++;
    }

    /**
     * Finishes the animation
     */
    public void finish() {
        if (!done)
            this.done = true;
    }

    /**
     * Returns whether the animation is finished
     * 
     * @return true if the animation has finished and is to be closed
     */
    public boolean finished() {
        return this.done && (this.inc % this.dur == 0);
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