package src.obj;

import java.awt.Font;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Canvas;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import java.util.LinkedList;
import java.util.function.Consumer;
import src.Window;
import src.GUI;
import src.Tuple.Pair;
import src.Tuple.Triple;

public class Header {
    private static final int offset = 4;
    private BufferedImage front, back;
    private LinkedList<LinkedList<Pair<BufferedImage, Short>>> bits;
    private LinkedList<Triple<Rectangle, Color, Short>> flares;
    private short t = 0;

    public Header(String header, Font font, Color front, Color back) {
        // Titles
        FontMetrics fm = new Canvas().getFontMetrics(font);
        final Consumer<Boolean> render = (fslashb) -> {
            BufferedImage img = new BufferedImage(fm.stringWidth(Window.TITLE), fm.getHeight() + fm.getDescent(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.getGraphics();
            g.setColor(fslashb ? front : back);
            g.setFont(font);
            g.drawString(Window.TITLE, offset, img.getHeight() - fm.getDescent());
            g.dispose();
            if (fslashb)
                this.front = img;
            else
                this.back = img;
        };
        render.accept(true);
        render.accept(false);
        // Header bits for animation
        final int width = 12, maxstart = 60;
        Random random = new Random();
        this.bits = new LinkedList<LinkedList<Pair<BufferedImage, Short>>>();
        for (int ln = 0; ln < this.front.getWidth(); ln += width) {
            LinkedList<Pair<BufferedImage, Short>> line = new LinkedList<Pair<BufferedImage, Short>>();
            for (int lnsum = 0; lnsum < this.front.getHeight();) {
                int height = width * (random.nextInt(5)+1);
                if (lnsum + height > this.front.getHeight())
                    height = this.front.getHeight() - lnsum;
                short start = (short) -random.nextInt(maxstart);
                BufferedImage shard = new BufferedImage(width + offset, height + width + offset, BufferedImage.TYPE_INT_ARGB);
                BufferedImage fr = new BufferedImage(width, height + width, BufferedImage.TYPE_INT_ARGB), bc = new BufferedImage(width, height + width, BufferedImage.TYPE_INT_ARGB);
                for (int b = 0; b < 2; b++) {
                    Graphics2D g = (Graphics2D) (b == 1 ? fr : bc).createGraphics();
                    g.drawImage(b == 1 ? this.front : this.back, -ln, -lnsum, null);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
                    g.drawPolygon(new int[] {0, width, 0}, new int[] {-1, -1, width-1}, 3);
                    g.drawPolygon(new int[] {0, width, width}, new int[] {height + width+1, height + width+1, height+1}, 3);
                    g.dispose();
                }
                Graphics g = shard.getGraphics();
                g.drawImage(bc, offset, 0, null);
                g.drawImage(fr, 0, offset, null);
                g.dispose();
                line.add(new Pair<BufferedImage, Short>(shard, start));
                lnsum += height;
            }
            this.bits.add(line);
        }
        // Random flares for the feeling
        this.flares = new LinkedList<Triple<Rectangle, Color, Short>>();
        for (int i = 0; i < random.nextInt(6) + 9; i++) {
            Rectangle rect = new Rectangle(random.nextInt(this.front.getWidth() - width * 2), this.front.getHeight() + random.nextInt(this.front.getHeight()), (int) (width * 1.5), width * (random.nextInt(4)+2));
            short start = (short) -random.nextInt(maxstart / 2);
            Color color = random.nextBoolean() ? front.brighter() : back.brighter();
            this.flares.add(new Triple<Rectangle, Color, Short>(rect, color, start));
        }
    }

    public BufferedImage render() {
        return simRender(0.0);
    }

    public BufferedImage simRender(double delta) {
        BufferedImage img;
        if (t == -1) {
            // Draw normal title
            img = new BufferedImage(front.getWidth() + offset, front.getHeight() + offset, BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.createGraphics();
            g.drawImage(this.back, offset, 0, null);
            g.drawImage(this.front, 0, offset, null);
            g.dispose();
        } else {
            // Draw animated title
            img = new BufferedImage(front.getWidth() + offset, (front.getHeight() + offset)*2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            // Draw bits
            int cx = 0, cy = 0;
            for (int x = 0; x < bits.size(); x++) {
                cy = front.getHeight() * 2 - 1;
                for (int y = bits.get(x).size(); y > 0; y--) {
                    Pair<BufferedImage, Short> tuple = bits.get(x).get(y - 1);
                    cy -= tuple.getAlpha().getHeight() - 12 - offset;
                    if (tuple.getBeta() >= 0)
                        g.drawImage(tuple.getAlpha(), cx, cy - GUI.smoothCurve(tuple.getBeta()+60, 120, front.getHeight()), null);
                }
                cx += 12;
            }
            // Draw flares
            for (int i = 0; i < flares.size(); i++) {
                Triple<Rectangle, Color, Short> triple = flares.get(i);
                if (triple.getGamma() < 0)
                    continue;
                Composite neutral = g.getComposite();
                if (triple.getGamma() < 15)
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, triple.getGamma() / 15f));
                else if (triple.getGamma() > 75 && triple.getGamma() < 90)
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (triple.getGamma() - 75) / 15f));
                else if (triple.getGamma() >= 90)
                    continue;
                g.setColor(triple.getBeta());
                g.fill(triple.getAlpha());
                g.setComposite(neutral);
            }
            g.dispose();
        }
        return img;
    }

    public void tick() {
        if (t >= 0 && t < 120) {
            t++;
            for (int x = 0; x < bits.size(); x++) {
                for (int y = 0; y < bits.get(x).size(); y++) {
                    if (bits.get(x).get(y).getBeta() < 60)
                        bits.get(x).get(y).setBeta((short) (bits.get(x).get(y).getBeta() + 1));
                }
            }
            for (int i = 0; i < flares.size(); i++) {
                if (flares.get(i).getGamma() <= 90) {
                    flares.get(i).setGamma((short)(flares.get(i).getGamma()+1));
                    Rectangle rect = flares.get(i).getAlpha();
                    rect.y -= 2;
                    flares.get(i).setAlpha(rect);
                }
            }
        } else if (t >= 120)
            t = -1;
    }
}
