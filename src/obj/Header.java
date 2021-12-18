package src.obj;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Canvas;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Random;
import java.util.LinkedList;
import java.util.function.Consumer;
import src.Window;
import src.Tuple;

public class Header {
    private static final int offset = 4;
    private BufferedImage front, back;
    private LinkedList<LinkedList<Tuple<BufferedImage, Short>>> bits;
    private short t = 0;

    /*static class Bit {
        private static final Random random = new Random();
        private Dimension dim;
        private int start;

        public Bit(int width, int maxstart) {
            this.dim = new Dimension(width, (random.nextInt(6)+1)*width);
            this.start = random.nextInt(maxstart);
        }

        public Bit(int width, int height, int maxstart) {
            this.dim = new Dimension(width, height);
            this.start = random.nextInt(maxstart);
        }

        public Dimension getDimension() {
            return this.dim;
        }

        public Polygon[] getPolygons() {
            int cs[][] = {{0, 0}, {0, dim.width}, {dim.width, 0}};
            int xs[] = new int[cs.length];
            int ys[] = new int[cs.length];
            Polygon top, bottom;
            for (int i = 0; i < cs.length; i++) {
                xs[i] = cs[i][0];
                ys[i] = cs[i][1];
            }
            top = new Polygon(xs, ys, cs.length);
            for (int i = 0; i < cs.length; i++) {
                xs[i] = dim.width - xs[i];
                ys[i] = dim.height - ys[i];
            }
            bottom = new Polygon(xs, ys, cs.length);
            return new Polygon[] {top, bottom};
        }

        public BufferedImage render(BufferedImage front, BufferedImage back) {
            BufferedImage img = new BufferedImage(front.getWidth() + offset, front.getWidth() + offset, BufferedImage.TYPE_INT_ARGB);
            return img;
        }

        public static int getTotalHeight(Bit[] bits) {
            int h = 0;
            for (Bit b : bits)
                h += b.getDimension().height;
            return h;
        }
    }*/

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
        final int width = 12, maxstart = 2 * 60;
        Random random = new Random();
        this.bits = new LinkedList<LinkedList<Tuple<BufferedImage, Short>>>();
        for (int ln = 0; ln < this.front.getHeight(); ln += width) {
            LinkedList<Tuple<BufferedImage, Short>> line = new LinkedList<Tuple<BufferedImage, Short>>();
            for (int lnsum = 0; lnsum < this.front.getHeight();) {
                int height = width * (random.nextInt(5)+1);
                short start = (short) random.nextInt(maxstart);
                BufferedImage shard = new BufferedImage(width + offset, height + width + offset, BufferedImage.TYPE_INT_ARGB);
                BufferedImage fr = new BufferedImage(width, height + width, BufferedImage.TYPE_INT_ARGB), bc = new BufferedImage(width, height + width, BufferedImage.TYPE_INT_ARGB);
                for (boolean b = false; !b; b = true) {
                    Graphics2D g = (Graphics2D) (b ? fr : bc).createGraphics();
                    g.drawImage(b ? this.front : this.back, ln, lnsum, null);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
                    g.drawPolygon(new int[] {0, width, 0}, new int[] {0, 0, width}, 3);
                    g.drawPolygon(new int[] {0, width, width}, new int[] {height + width, height + width, height}, 3);
                    g.dispose();
                }
                Graphics g = shard.getGraphics();
                g.drawImage(fr, 0, offset, null);
                g.drawImage(bc, offset, 0, null);
                g.dispose();
                line.add(new Tuple<BufferedImage, Short>(shard, start));
                lnsum += height;
            }
            this.bits.add(line);
        }
        // Random flares for the feeling

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
        } else {
            // Draw animated title
            img = new BufferedImage((front.getWidth() + offset)*2, (front.getHeight() + offset)*2, BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.createGraphics();
            int cx = 0, cy = 0;
            for (int x = 0; x < bits.size(); x++) {
                cy = img.getHeight();
                for (int y = bits.get(x).size(); y > 0; --y) {
                    Tuple<BufferedImage, Short> tuple = bits.get(x).get(y);
                    cy -= tuple.getAlpha().getHeight() - 12 - offset;
                    g.drawImage(tuple.getAlpha(), cx, cy, null);
                }
                cx += 12;
            }
        }
        return img;
    }

    public void tick() {
        if (t >= 0 && t < 4 * 60)
            t++;
        else if (t >= 4 * 60)
            t = -1;
        for (int x = 0; x < bits.size(); x++) {
            for (int y = 0; y < bits.get(x).size(); y++) {
                bits.get(x).get(y).setBeta(bits.get(x).get(y).getBeta());
            }
        }
    }
}
