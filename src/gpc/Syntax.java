package src.gpc;

import java.net.URL;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Font;
import java.awt.Canvas;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import src.utl.Lambda.Yield;
import src.utl.Tuple.Pair;

public abstract class Syntax {
    
    public static Font font;
    public static int spacing = 12;
    public static Color color = Color.WHITE;
    public static Color code = new Color(11, 255, 131), highlight = new Color(255, 254, 6), link = new Color(25, 254, 255);
    public static int width;

    public static final char[][] formatting = new char[][] {
        {'*', '*'}, {'*', '\0'}, {'_', '_'}, {'_', '\0'}, {'~', '~'}, {'`', '\0'}, {'~', '\0'}, {'^', '\0'}, {'[', '^'}, {'=', '='}
    };

    protected Rectangle dimension;

    private static LinkedList<Link> links = new LinkedList<Link>();

    /**
     * Checks whether the markdown matches the pattern
     * 
     * @param lines the markdown to parse
     * @return true if the markdown fits the syntax
     */
    public static boolean check(String lines) {return false;}

    /**
     * Simulates the rendering of the text inside a rectangular box, thereby determining the minimum width and the required height. It also logs all links with the given rectangle coordinates
     * 
     * @param box a rectangle with the coordinates and the width of the text box, height will be determined
     * @param f   the font of the text
     * @param str the text to render
     * @return    the minimum width (only useful with multiline text) and the required height
     */
    public static Dimension rawText(Rectangle box, Font f, String str) {
        int height = 0, width = 0;
        String line = "";
        FontMetrics fm = new Canvas().getFontMetrics(f);
        boolean escflag = false;
        for (int c = 0; c < str.length(); c++) {
            if ((str.charAt(c) == '\s' && fm.stringWidth(line + str.substring(c, str.indexOf(' ', c + 1) == -1 ? str.length() : str.indexOf(' ', c + 1))) > box.width) || str.charAt(c) == '\n') {
                if (fm.stringWidth(line) > width)
                    width = fm.stringWidth(line);
                line = "";
                height += fm.getHeight();
            }
            if (escflag) {
                escflag = false;
                line += c;
                continue;
            }
            if (c == '\\') {
                escflag = true;
                continue;
            }
            boolean skip = false;
            for (char[] special : formatting) {
                if (special[0] == str.charAt(c)) {
                    if (special[1] == '\0') {
                        skip = true;
                        break;
                    } else if (c < str.length() - 1 && special[1] == str.charAt(c+1)) {
                        c++;
                        skip = true;
                        break;
                    }
                }
            }
            if (skip)
                continue;
            if (Link.check(str.substring(c))) {
                Matcher m = Link.pattern.matcher(str.substring(c));
                m.find();
                String al = m.group(1), be = m.group(2), ga = m.groupCount() == 3 ? m.group(3) : null;
                links.add(new Link(be, new Rectangle(fm.stringWidth(line) + box.x, height + box.y, fm.stringWidth(al), fm.getHeight()), ga));
                line += al;
                c += m.group(0).length() - 1;
                continue;
            }
            line += str.charAt(c);
        }
        if (line.length() > 0)
            height += fm.getHeight();
        if (width == 0)
            width = fm.stringWidth(line);
        return new Dimension(width, height);
    }

    /**
     * Renders a string of text inside a rectangle using the given graphics object
     * 
     * @param box the rectangle in which to render
     * @param g   a graphics object to use for the render
     * @param str the text to render
     * @return    the height of the render text box (this is only optional)
     */
    protected int renderText(Rectangle box, Graphics g, String str) {
        FontMetrics fm = g.getFontMetrics();
        g.setColor(color);
        boolean _escape = false, _strike = false, _link = false;
        int x = box.x, y = box.y;
        for (int c = 0; c < str.length(); c++) {
            char ch = str.charAt(c), nx = c < str.length() - 1 ? str.charAt(c + 1) : '\0';
            if((ch == '\s' && fm.stringWidth(str.substring(c, str.indexOf(' ', c + 1) == -1 ? str.length() : str.indexOf(' ', c + 1))) + x > box.width + box.x) || ch == '\n') {
                y += fm.getHeight();
                x = box.x;
                continue;
            }
            if (!_escape) {
                if (ch == '\\') {
                    // Escaped characters
                    _escape = true;
                    continue;
                } else if ((ch == '*' && nx == '*') || (ch == '_' && nx == '_')) {
                    // Bold
                    g.setFont(g.getFont().deriveFont(g.getFont().getStyle() == Font.PLAIN ? Font.BOLD : Font.PLAIN));
                    c++;
                    continue;
                } else if (ch == '*' || ch == '_') {
                    // Italic
                    g.setFont(g.getFont().deriveFont(g.getFont().getStyle() == Font.PLAIN ? Font.ITALIC : Font.PLAIN));
                    continue;
                } else if (ch == '~' && nx == '~') {
                    // Strikethrough
                    _strike = !_strike;
                    c++;
                    continue;
                } else if (ch == '`') {
                    // Code
                    g.setColor(g.getColor() == code ? color : code);
                    continue;
                } else if (ch == '~') {
                    // Subscript
                    float size = g.getFont().getSize();
                    g.setFont(g.getFont().deriveFont((float) (g.getFont().getSize() / 1.5)));
                    FontMetrics fm2 = g.getFontMetrics();
                    while (str.charAt(++c) != '~') {
                        g.drawString(Character.toString(str.charAt(c)), x, y + fm.getHeight());
                        x += fm2.stringWidth(Character.toString(str.charAt(c)));
                    }
                    g.setFont(g.getFont().deriveFont(size));
                    continue;
                } else if (ch == '^' ) {
                    // Superscript
                    float size = g.getFont().getSize();
                    g.setFont(g.getFont().deriveFont((float) (g.getFont().getSize() / 1.5)));
                    FontMetrics fm2 = g.getFontMetrics();
                    while (str.charAt(++c) != '^') {
                        g.drawString(Character.toString(str.charAt(c)), x, y + (fm.getAscent() + fm.getLeading()));
                        x += fm2.stringWidth(Character.toString(str.charAt(c)));
                    }
                    g.setFont(g.getFont().deriveFont(size));
                    continue;
                } else if (ch == '[' && nx == '^') {
                    // Footnote
                    float size = g.getFont().getSize();
                    g.setFont(g.getFont().deriveFont((float) (g.getFont().getSize() / 1.5)));
                    FontMetrics fm2 = g.getFontMetrics();
                    do {
                        if (str.charAt(c) == '^' || str.charAt(c) == '[')
                            continue;
                        g.drawString(Character.toString(str.charAt(c)), x, y + (fm.getAscent() + fm.getLeading()));
                        x += fm2.stringWidth(Character.toString(str.charAt(c)));
                    } while (str.charAt(++c) != ']');
                    g.setFont(g.getFont().deriveFont(size));
                    continue;
                } else if (ch == '[') {
                    // Link open
                    Matcher m = Link.pattern.matcher(str.substring(c));
                    if (m.find()) {
                        _link = true;
                        g.setColor(link);
                        continue;
                    }
                } else if (ch == ']' && _link) {
                    // Link close
                    Matcher m = Pattern.compile("^\\(((?:https:\\/\\/)?(?:\\w+\\.)?.+\\.\\w+\\/?)(?: \"(.+)\")?\\)").matcher(str.substring(c+1));
                    if (m.find()) {
                        g.setColor(color);
                        _link = false;
                        c += m.group(0).length();
                        continue;
                    }
                } else if (ch == '=' && nx == '=') {
                    // Highlight
                    g.setColor(g.getColor() == highlight ? color : highlight);
                    c++;
                    continue;
                }
            } else
                _escape = false;
            g.drawString(Character.toString(ch), x, y + fm.getHeight());
            if (_strike)
                g.drawLine(x, y + fm.getAscent() + fm.getLeading()/2,
                        x + fm.stringWidth(Character.toString(c)),
                        y + fm.getAscent() + fm.getLeading()/2);
            x += fm.stringWidth(Character.toString(ch));
        }
        return y + fm.getHeight() - box.y;
    }

    public abstract void render(Graphics g, int y);

    /**
     * The height of the component
     * 
     * @return
     */
    public int getHeight() {
        return dimension.height;
    }

    /**
     * The width of the component
     * 
     * @return
     */
    public int getWidth() {
        return dimension.width;
    }

    /**
     * A simple markdown paragraph; supported styles are <i>bold, italic, strikethrough, code, subscript, superscript</i> and <i>highlight</i>.
     */
    public static class Paragraph extends Syntax {
        public static final Pattern pattern = Pattern.compile("^.*\\n?$");
        public String text;

        public Paragraph(String markdown, Point point) {
            this.text = markdown.substring(0, markdown.contains("\n") ? markdown.indexOf("\n") : markdown.length());
            this.dimension = new Rectangle(point, simulate(point));
        }

        private Dimension simulate(Point point) {
            return new Dimension(rawText(new Rectangle(point.x, point.y, Syntax.width, -1), Syntax.font, this.text));
        }

        public void render(Graphics g, int y) {
            g.setColor(color);
            g.setFont(font);
            renderText(new Rectangle(0, y, Syntax.width, dimension.height), g, text);
        }

        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }
    }

    /**
     * An image in the markdown
     */
    public static class MarkdownImage extends Syntax {
        public static final Pattern pattern = Pattern.compile("^!\\[\\]\\((\\S+)(?:\\s\"(.+)\")?\\)\\n?$");
        private Image image;
        private String hover;

        public MarkdownImage(String image, Point point) {
            try { 
                Matcher m = pattern.matcher(image);
                m.find();
                URL url = new URL(m.group(1));
                this.image = ImageIO.read(url);
                this.hover = m.groupCount() == 2 ? m.group(2) : null;
                this.dimension = new Rectangle(point, this.simulate(point));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Dimension simulate(Point point) {
            if (image.getWidth(null) <= Syntax.width)
                return new Dimension(image.getWidth(null), image.getHeight(null));
            else
                return new Dimension(Syntax.width, image.getHeight(null) * Syntax.width / image.getWidth(null));
        }

        public void render(Graphics g, int y) {
            g.setColor(color);
            g.setFont(font);
            g.drawImage(image, 0, y, dimension.width, dimension.height, null);
        }

        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }

        public String getHoverString() {
            return hover;
        }
    }

    /**
     * A header of a level between one and five
     */
    public static class Heading extends Syntax {
        public static final Pattern pattern = Pattern.compile("^(#{1,5})\\s?([\\w\\d\\s]*?)\\n?$");
        private String text;
        private int level;

        public Heading(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            this.level = 6 - m.group(1).length();
            this.text = m.group(2);
            this.dimension = new Rectangle(point, simulate(point));
        }

        private Dimension simulate(Point point) {
            return rawText(new Rectangle(point.x, point.y, Syntax.width, -1), Syntax.font.deriveFont((float) (Syntax.font.getSize() * (1.0 + level / 5.0))), this.text);
        }

        public void render(Graphics g, int y) {
            g.setColor(color);
            g.setFont(Syntax.font.deriveFont((float) (Syntax.font.getSize() * (1.0 + level / 5.0))));
            renderText(new Rectangle(0, y, Syntax.width, dimension.height), g, text);
        }
        
        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }
    }

    /**
     * A list of either sorted or unsorted numeration
     */
    public static class List extends Syntax {
        public static final Pattern pattern = Pattern.compile("^([-\\*\\d\\.]){1,2} .+?\\n(?:\\d?\\1 .+\\n?)*");
        protected String[] items;

        public List(String[] items) {
            this.items = items;
        }

        protected Dimension simulate(Point point) {
            String list = "";
            for (String item : items)
                list += item + "\n";
            return rawText(new Rectangle(point.x, point.y, Syntax.width - 20, -1), Syntax.font, list.strip());
        }

        public void render(Graphics g, int y) {
            g.setColor(color);
            g.setFont(font);
            String list = "";
            for (String item : items)
                list += item + "\n";
            renderText(new Rectangle(16, y, dimension.width, dimension.height), g, list.strip());
        }

        /**
         * An ordered list with digits as identifiers
         */
        public static class OrderedList extends List {
            public static final Pattern pattern = Pattern.compile("^\\d\\. .+?\\n(?:\\d\\.\\s?.+\\n?)*");

            public OrderedList(String markdown, Point point) {
                super(extract(markdown));
                this.dimension = new Rectangle(point, simulate(point));
            }

            private static String[] extract(String md) {
                Matcher m = pattern.matcher(md);
                m.find();
                LinkedList<String> items = new LinkedList<String>();
                for (String item : m.group(0).split("\\R"))
                    items.add(item.substring(1).strip());
                String[] result = new String[items.size()];
                for (int i = 0; i < items.size(); i++)
                    result[i] = items.get(i);
                return result;
            }
            
            public static boolean check(String lines) {
                return pattern.matcher(lines).find();
            }

            public void render(Graphics g, int y) {
                g.setColor(color);
                g.setFont(font);
                int c = 0;
                for (String item : items) {
                    String iden = " %d. ".formatted(++c);
                    g.drawString(iden, 20 - g.getFontMetrics().stringWidth(iden), y + g.getFontMetrics().getHeight());
                    y += renderText(new Rectangle(20, y, Syntax.width - 20, -1), g, item.substring(item.indexOf('.') + 1).strip());
                }
            }
        }

        /**
         * An unordered list with either asterisks or hyphens instead of identifiers
         */
        public static class UnorderedList extends List {
            public static final Pattern pattern = Pattern.compile("^([-\\*]) .+?\\n(?:\\1\\s?.+\\n?)*");

            public UnorderedList(String markdown, Point point) {
                super(extract(markdown));
                this.dimension = new Rectangle(point, simulate(point));
            }

            private static String[] extract(String md) {
                Matcher m = pattern.matcher(md);
                m.find();
                LinkedList<String> items = new LinkedList<String>();
                for (String item : m.group(0).split("\\R"))
                    items.add(item.substring(1).strip());
                String[] result = new String[items.size()];
                for (int i = 0; i < items.size(); i++)
                    result[i] = items.get(i);
                return result;
            }
    
            public static boolean check(String lines) {
                return pattern.matcher(lines).find();
            }

            public void render(Graphics g, int y) {
                g.setColor(color);
                g.setFont(font);
                for (String item : items) {
                    int rad = 6;
                    g.fillOval((20 - rad) / 2, y + g.getFontMetrics().getHeight() - (g.getFontMetrics().getHeight() - rad) / 2, rad, rad);
                    y += renderText(new Rectangle(20, y, Syntax.width - 20, -1), g, item);
                }
            }
        }

        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }
    }

    /**
     * A quote paragraph induced by a greater-than-sign
     */
    public static class Quote extends Syntax {
        public static final Pattern pattern = Pattern.compile("^>\\s?(.+\\n(?:(?:>\\s?)?.+\\n)*)");
        private String[] text;

        public Quote(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            this.text = m.group(1).split("\\R");
            this.dimension = new Rectangle(point, simulate(point));
        }

        private Dimension simulate(Point point) {
            String quote = "";
            for (String line : text)
                quote += line + "\n";
            return rawText(new Rectangle(point.x, point.y, Syntax.width - 12, -1), Syntax.font, quote.strip());
        }

        public void render(Graphics g, int y) {
            g.setColor(color);
            g.setFont(font);
            String total = "";
            for (String line : text)
                total += line + "\n";
            int h = renderText(new Rectangle(12, y, Syntax.width - 12, -1), g, total.strip());
            g.fillRect(0, y + g.getFontMetrics().getDescent(), 2, h);
        }
        
        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }
    }

    /**
     * Three hyphens for a horizontal rule for example to divide the markdown into sections
     */
    public static class Rule extends Syntax {
        public static final Pattern pattern = Pattern.compile("^---\\n?$");

        public Rule(String markdown, Point point) {
            this.dimension = new Rectangle(point, simulate(point));
        }

        private Dimension simulate(Point point) {
            return new Dimension(Syntax.width, 2);
        }

        public void render(Graphics g, int y) {
            g.setColor(color);
            g.fillRect(0, y, Syntax.width, 2);
        }

        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }
    }

    /**
     * A table.
     */
    public static class Table extends Syntax {
        public static final Pattern pattern = Pattern.compile("^(\\|(?:\\s*.+?\\s*\\|)+?)\\n\\|(?:\\s*\\-+?\\s*\\|)+\\n(\\|(?:\\s*.+?\\s*\\|)+\\n?)+");
        private int columns, rows;
        private String[][] table;
        private int[] fieldX, fieldY;

        public Table(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            String first = m.group(1), second = "";
            if (m.groupCount() >= 2)
                second = m.group(2);
            this.rows = -1;
            for (char c : first.toCharArray()) {
                if (c == '|')
                    rows++;
            }
            this.columns = (first + "\n" + second).split("\\R").length;
            this.table = new String[this.columns][this.rows];
            String[] column = (first + "\n" + second).split("\\R");
            for (int i = 0; i < this.columns; i++) {
                String[] row = column[i].split("\\|");
                int k = 0;
                for (int j = 0; j < row.length; j++) {
                    if (row[j].strip().length() > 0 && k < this.rows) {
                        this.table[i][k++] = row[j].strip();
                    }
                }
            }
            this.fieldY = new int[this.columns];
            this.fieldX = new int[this.rows];
            this.dimension = new Rectangle(point, simulate(point));
        }

        private Dimension simulate(Point point) {
            int width = (Syntax.width / rows) - (rows * 7 + 1), height;
            for (int col = 0; col < this.table.length; col++) {
                for (int row = 0; row < this.table[col].length; row++) {
                    Dimension d = rawText(new Rectangle(width * (col + 7) + 1, point.y, width, -1), col == 0 ? Syntax.font.deriveFont(Font.BOLD) : Syntax.font, this.table[col][row]);
                    if (d.width > fieldX[row])
                        fieldX[row] = d.width;
                    if (d.height > fieldY[col])
                        fieldY[col] = d.height;
                }
            }
            width = 1;
            height = 1;
            for (int x = 0; x < fieldX.length; x++)
                width += fieldX[x] + 7;
            for (int y = 0; y < fieldY.length; y++)
                height += fieldY[y] + 7;
            return new Dimension(width, height);
        }

        public void render(Graphics g, int y) {
            int x;
            for (int col = 0; col < this.table.length; col++) {
                x = 4;
                if (col == 0)
                    g.setFont(font.deriveFont(Font.BOLD));
                else
                    g.setFont(font);
                for (int row = 0; row < this.table[col].length; row++) {
                    renderText(new Rectangle(x, y - g.getFontMetrics().getDescent(), fieldX[row], fieldY[col]), g, this.table[col][row]);
                    x += fieldX[row] + 7;
                }
                g.setColor(Color.LIGHT_GRAY.brighter());
                g.drawLine(0, y - 3, dimension.width, y - 3);
                y += fieldY[col] + 7;
            }
            g.drawLine(0, y - 3, dimension.width, y - 3);
        }
        
        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }
    }

    /**
     * A term and its definition(s)
     */
    public static class Definition extends Syntax {
        public static final Pattern pattern = Pattern.compile("^(.*?)\\n((?:\\:\\s?.*\\n?)+)");
        private String term;
        private String[] definition;

        public Definition(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            this.term = m.group(1);
            this.definition = m.group(2).split("\\R");
            for (int i = 0; i < this.definition.length; i++)
                this.definition[i] = this.definition[i].replaceFirst("\\:\\s*", "");
            this.dimension = new Rectangle(point, simulate(point));
        }

        private Dimension simulate(Point point) {
            String quote = term;
            for (String line : definition)
                quote += "\n" + line;
            return rawText(new Rectangle(point.x, point.y, Syntax.width - 16, -1), Syntax.font, quote.strip());
        }

        public void render(Graphics g, int y) {
            g.setColor(color);
            g.setFont(font);
            y += renderText(new Rectangle(0, y, Syntax.width, -1), g, term);
            g.setFont(font.deriveFont(Font.ITALIC));
            String quote = "";
            for (String line : definition)
                quote += "\n" + line;
            y += renderText(new Rectangle(16, y, Syntax.width - 16, -1), g, quote.strip());
        }
        
        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }
    }

    /**
     * A codeblock or an escaped paragraph
     */
    public static class CodeBlock extends Syntax {
        public static final Pattern pattern = Pattern.compile("^```(\\w+)?\\n((?:.*\\n)*?)```");
        private String language, code;

        public CodeBlock(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            if (m.groupCount() == 1) {
                language = null;
                code = m.group(1).strip();
            } else {
                language = m.group(1);
                code = m.group(2).strip();
            }
            this.dimension = new Rectangle(point, simulate(point));
        }

        private Dimension simulate(Point point) {
            int height = 0;
            FontMetrics fm = new Canvas().getFontMetrics(Syntax.font);
            String line = "";
            for (char c : this.code.toCharArray()) {
                if (fm.stringWidth(line + c) > Syntax.width || c == '\n') {
                    height += fm.getHeight();
                    line = "";
                }
                line += c;
            }
            if (line.length() > 0)
                height += fm.getHeight();
            return new Dimension(Syntax.width, height);
        }

        public void render(Graphics g, int y) {
            g.setColor(Syntax.code);
            g.setFont(font);
            int x = 0;
            int tab = g.getFontMetrics().stringWidth("\s\s\s\s");
            for (char c : code.toCharArray()) {
                if (c == '\t') {
                    x = (int) (x / tab + 1) * tab;
                    continue;
                }
                if (c == '\n' || x + g.getFontMetrics().stringWidth(Character.toString(c)) > Syntax.width) {
                    x = 0;
                    y += g.getFontMetrics().getHeight();
                }
                g.drawString(Character.toString(c), x, y + g.getFontMetrics().getHeight());
                x += g.getFontMetrics().stringWidth(Character.toString(c));
            }
        }
        
        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }

        public String getLanguage() {
            return language;
        }
    }
    
    /**
     * A footnote, preferably inserted at the end of the markdown
     */
    public static class Footnote extends Syntax {
        public static final Pattern pattern = Pattern.compile("^\\[\\^(\\d+|\\w+)\\]:\\s?(.*)\\n?$");
        private String note, identifier = null;

        public Footnote(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            identifier = m.group(1);
            this.note = m.group(2);
            this.dimension = new Rectangle(point, simulate(point));
        }

        private Dimension simulate(Point point) {
            return rawText(new Rectangle(point.x, point.y, Syntax.width, -1), Syntax.font, "[%s]: %s".formatted(identifier, note));
        }

        public void render(Graphics g, int y) {
            g.setColor(color);
            g.setFont(font);
            renderText(new Rectangle(0, y, Syntax.width, -1), g, identifier + ". " + note);
        }
        
        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }
    }

    public static class Link {
        public static final Pattern pattern = Pattern.compile("^\\[(.+)\\]\\(((?:https:\\/\\/)?(?:\\w+\\.)?.+\\.\\w+\\/?)(?: \"(.+)\")?\\)");
        private URL url;
        private Rectangle box;
        private String hover;
    
        public Link(String url, Rectangle box) {
            try {
                this.url = new URL(url);
            } catch(Exception e) {
                e.printStackTrace();
            }
            this.box = box;
            this.hover = null;
        }

        public Link(String url, Rectangle box, String hover) {
            try {
                this.url = new URL(url);
            } catch(Exception e) {
                e.printStackTrace();
            }
            this.box = box;
            this.hover = hover;
        }

        public URL getURL() {
            return url;
        }

        public Rectangle getActionBox() {
            return box;
        }

        public String getHoverString() {
            return hover;
        }

        /**
         * Checks whether the markdown matches the pattern
         * 
         * @param lines the markdown to parse
         * @return true if the markdown fits the syntax
         */
        public static boolean check(String lines) {
            return pattern.matcher(lines).find();
        }
    }

    public static Syntax[] compile(String[] markdown) {
        LinkedList<Syntax> syntax = new LinkedList<Syntax>();

        Yield<Pair<String[], Integer>, String> join = (array) -> {
            String joined = "";
            for (int i = array.getBeta(); i < array.getAlpha().length; i++)
                joined += array.getAlpha()[i] + "\n";
            return joined.strip();
        };
        int y = 0;
        // Iterate the markdown
        try {
            for (int l = 0; l < markdown.length;) {
                String here = markdown[l], next = join.yield(new Pair<String[], Integer>(markdown, l));
                if (here.strip().length() == 0) {
                    l++;
                    continue;
                }
                // Assigning a section type
                for (Class<? extends Syntax> cls : convert(new Class<?>[] {
                    MarkdownImage.class, Rule.class, Quote.class, CodeBlock.class, Table.class, Definition.class,
                    List.OrderedList.class, List.UnorderedList.class, Footnote.class, Heading.class, Paragraph.class
                })) {
                    if ((Boolean) cls.getMethod("check", String.class).invoke(null, next)) {
                        // Single-line elements
                        Syntax element = cls.getConstructor(String.class, Point.class).newInstance(next, new Point(0, y));
                        syntax.add(element);
                        y += element.getHeight() + spacing;
                        while (l < markdown.length && markdown[l].strip().length() > 0)
                            l++;
                        break;
                    } else if ((Boolean) cls.getMethod("check", String.class).invoke(null, here)) {
                        // Multi-line elements
                        Syntax element = cls.getConstructor(String.class, Point.class).newInstance(here, new Point(0, y));
                        syntax.add(element);
                        y += element.getHeight() + spacing;
                        l++;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Syntax[] array = new Syntax[syntax.size()];
        for (int i = 0; i < syntax.size(); i++)
            array[i] = syntax.get(i);
        return array;
    }

    private static final ArrayList<Class<? extends Syntax>> convert(Class<?>[] array) {
        ArrayList<Class<? extends Syntax>> extend = new ArrayList<Class<? extends Syntax>>();
        for (Class<?> type : array) {
            extend.add(type.asSubclass(Syntax.class));
        }
        return extend;
    }

    public static Link[] getLinks() {
        Link[] array = new Link[links.size()];
        for (int i = 0; i < links.size(); i++)
            array[i] = links.get(i);
        return array;
    }

}
