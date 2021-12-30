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
    private int ticks, total;

    public LoadingBar(Dimension size, int ticks, Font font) {
        super(size);
        this.font = font;
        this.ticks = 0;
        this.total = ticks;
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
        int w = (dimension.width-13) * ticks / total;
        g.setColor(color3);
        g.fillRect(6, 6+banner, w, dimension.height-13);
        g.setColor(Color.WHITE);
        g.fillRect(6 + (w > dimension.width - 17 ? dimension.width - 17 : w), 6+banner, 4, dimension.height-13);
        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString((ticks < total ? "Loading resources..." : "Download complete!").toUpperCase(), 0, g.getFontMetrics().getHeight());
        return img;
    }

    public void tick() {
        if (ticks < total)
            this.ticks++;
    }

    public void setProgress(int ticks) {
        if (ticks <= total && ticks >= 0)
            this.ticks = ticks;
    }

    public void setProgress(double percentage) {
        if (percentage >= 0.0 && percentage <= 1.0)
            this.ticks = (int) (percentage * total);
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