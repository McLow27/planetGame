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
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import src.utl.Lambda.Yield;
import src.utl.Tuple.Pair;

public abstract class Syntax {
    
    public static Font font;
    public static int spacing = 2;
    public static Color color = Color.WHITE;
    public static Color code = new Color(11, 255, 131), highlight = new Color(255, 254, 6), link = new Color(25, 254, 255);
    public static int width;

    public static final char[][] formatting = new char[][] {
        {'*', '*'}, {'*', '\0'}, {'~', '~'}, {'`', '\0'}, {'~', '\0'}, {'^', '\0'}
    };

    protected static Pattern pattern;
    protected Rectangle dimension;

    private static LinkedList<Link> links = new LinkedList<Link>();
    private static LinkedList<Footnote> footnotes = new LinkedList<Footnote>();

    public abstract Dimension simulate(Point point);
    
    /**
     * Checks whether the markdown matches the pattern
     * 
     * @param lines the markdown to parse
     * @return true if the markdown fits the syntax
     */
    public static boolean check(String lines) {
        return pattern.matcher(lines).find();
    }

    public static Dimension rawText(Rectangle box, Font f, String str) {
        int height = box.height;
        String line = "";
        FontMetrics fm = new Canvas().getFontMetrics(f);
        boolean escflag = false;
        for (int c = 0; c < str.length(); c++) {
            if (str.charAt(c) == ' ' && fm.stringWidth(line + str.substring(c, str.indexOf(' ', c) - c)) > box.width) {
                line = "";
                height += fm.getHeight();
            }
            if (escflag) {
                escflag = false;
                line += c;
                continue;
            }
            if (c == '\\')
                escflag = true;
            for (char[] special : formatting) {
                if (special[0] == str.charAt(c) && (special[1] != '\0' && c < str.length() - 1 ? (special[1] == str.charAt(c+1)) : true))
                    continue;
            }
            if (Link.check(str.substring(c))) {
                Matcher m = Link.pattern.matcher(str.substring(c));
                m.find();
                String al = m.group(1), be = m.group(2), ga = m.groupCount() == 3 ? m.group(3) : null;
                links.add(new Link(be, new Rectangle(fm.stringWidth(line) + box.width, height + box.height, fm.stringWidth(al), fm.getHeight()), ga));
                line += al;
                c += m.group(0).length() - 1;
                continue;
            }
            line += str.charAt(c);
        }
        if (line.length() > 0)
            height += fm.getHeight();
        return new Dimension(Syntax.width, height);
    }

    public int getHeight() {
        return dimension.height;
    }

    public int getWidth() {
        return dimension.width;
    }

    public static class Paragraph extends Syntax {
        public static final Pattern pattern = Pattern.compile("^.*\\n?");
        public String text;

        public Paragraph(String markdown, Point point) {
            this.text = markdown.substring(0, markdown.indexOf("\n"));
            this.dimension = new Rectangle(point, simulate(point));
        }

        public Dimension simulate(Point point) {
            return new Dimension(rawText(new Rectangle(point.x, point.y, Syntax.width, -1), Syntax.font, this.text));
        }
    }

    public static class MarkdownImage extends Syntax {
        public static final Pattern pattern = Pattern.compile("^!\\[\\]\\((\\S+)(?:\\s\"(.+)\")?\\)\\n?");
        private Image image;
        private String hover;

        public MarkdownImage(String image, Point point) {
            try { 
                Matcher m = pattern.matcher(image);
                m.matches();
                URL url = new URL(m.group(1));
                this.image = ImageIO.read(url);
                this.hover = m.groupCount() == 2 ? m.group(2) : null;
                this.dimension = new Rectangle(point, this.simulate(point));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Dimension simulate(Point point) {
            if (image.getWidth(null) <= Syntax.width)
                return new Dimension(image.getWidth(null), image.getHeight(null));
            else
                return new Dimension(Syntax.width, image.getHeight(null) * Syntax.width / image.getWidth(null));
        }
    }

    public static class Heading extends Syntax {
        public static final Pattern pattern = Pattern.compile("^(#{1,5})\\s?([\\w\\d\\s]*?)\\n?");
        private String text;
        private int level;

        public Heading(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            this.level = m.group(1).length();
            this.text = m.group(2);
            this.dimension = new Rectangle(point, simulate(point));
        }

        public Dimension simulate(Point point) {
            return rawText(new Rectangle(point.x, point.y, Syntax.width, -1), Syntax.font.deriveFont((int) (Syntax.font.getSize() * (1.0 + level / 5.0))), this.text);
        }
    }

    public static class List extends Syntax {
        public static final Pattern pattern = Pattern.compile("^([-\\*\\d\\.]){1,2} .+?\\n(?:\\d?\\1 .+\\n?)*");
        private String[] items;

        public static class OrderedList extends List {
            public static final Pattern pattern = Pattern.compile("^\\d\\. .+?\\n(?:\\d\\. .+\\n?)*");
        }

        public static class UnorderedList extends List {
            public static final Pattern pattern = Pattern.compile("^([-\\*]) .+?\n(?:\1 .+\n?)*");
        }
    }

    public static class Quote extends Syntax {
        public static final Pattern pattern = Pattern.compile("^>\\s?(.+(?:(?:>\\s)?.+?\\n)*)");
        private String[] text;

        public Quote(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            this.text = m.group(1).split("\\R");
            this.dimension = new Rectangle(point, simulate(point));
        }

        public Dimension simulate(Point point) {
            String quote = "";
            for (String line : text)
                quote += line + "\n";
            return rawText(new Rectangle(point.x, point.y, Syntax.width - 16, -1), Syntax.font, quote.strip());
        }
    }

    public static class Rule extends Syntax {
        public static final Pattern pattern = Pattern.compile("^---$");

        public Rule(String markdown, Point point) {
            this.dimension = new Rectangle(point, simulate(point));
        }

        public Dimension simulate(Point point) {
            return new Dimension(Syntax.width, 4);
        }
    }

    public static class Table extends Syntax {
        public static final Pattern pattern = Pattern.compile("^(\\|(?:\\s*.+?\\s*\\|)+?)\\n\\|(?:\\s*\\-+?\\s*\\|)+\\n(\\|(?:\\s*.+?\\s*\\|)+\\n?)+", Pattern.MULTILINE);
        private int columns, rows;
        private String[][] table;
    }

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

        public Dimension simulate(Point point) {
            String quote = term;
            for (String line : definition)
                quote += "\n" + line;
            return rawText(new Rectangle(point.x, point.y, Syntax.width - 16, -1), Syntax.font, quote.strip());
        }
    }

    public static class CodeBlock extends Syntax {
        public static final Pattern pattern = Pattern.compile("^```(\\w+)?\\n((?:.*\\n)*?)```");
        private String language, code;

        public CodeBlock(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            if (m.groupCount() == 1) {
                language = null;
                code = m.group(1);
            } else {
                language = m.group(1);
                code = m.group(2);
            }
            this.dimension = new Rectangle(point, simulate(point));
        }

        public Dimension simulate(Point point) {
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
    }
    
    public static class Footnote extends Syntax {
        public static final Pattern pattern = Pattern.compile("^\\[\\^(\\d|\\w+)\\]:\\s?(.*)");
        private int num;
        private String note, identifier = null;

        public Footnote(String markdown, Point point) {
            Matcher m = pattern.matcher(markdown);
            m.find();
            try {
                identifier = m.group(1);
                num = Integer.parseInt(m.group(1));
            } catch (Exception e) {
                identifier = m.group(1);
                num = footnotes.size();
            }
            this.note = m.group(2);
            this.dimension = new Rectangle(point, simulate(point));
        }

        public Dimension simulate(Point point) {
            return rawText(new Rectangle(point.x, point.y, Syntax.width, -1), Syntax.font, "[%s]: %s".formatted(identifier, note));
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
        for (int l = 0; l < markdown.length; l++) {
            String next = join.yield(new Pair<String[], Integer>(markdown, l));
            // Assigning a section type
            for (Class<? extends Syntax> cls : convert(new Class<?>[] {
                MarkdownImage.class, Rule.class, Quote.class, List.class, CodeBlock.class, Table.class, Definition.class,
                List.OrderedList.class, List.UnorderedList.class, Footnote.class, Heading.class, Paragraph.class
            })) {
                try {
                    if ((Boolean) cls.getMethod("check", String.class).invoke(null, next)) {
                        Syntax element = cls.getConstructor(String.class, Point.class).newInstance(next, new Point(0, y));
                        syntax.add(element);
                        y += element.getHeight() + spacing;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Syntax[] array = new Syntax[syntax.size()];
        for (int i = 0; i < syntax.size(); i++)
            array[i] = syntax.get(i);
        return array;
    }

    private static final ArrayList<Class<? extends Syntax>> convert(Class<?>[] array) {
        ArrayList<Class<? extends Syntax>> extend = new ArrayList<Class<? extends Syntax>>();
        for (Class<?> type : array) {
            type.asSubclass(Syntax.class);
        }
        return extend;
    }

}
