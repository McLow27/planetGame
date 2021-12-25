package src.gpc;

import java.net.URL;
import java.awt.Rectangle;
import java.awt.Image;
import java.util.LinkedList;
import java.util.regex.Pattern;

public abstract class Syntax {
    
    protected static Pattern pattern;

    public class Paragraph extends Syntax {
        public static final Pattern pattern = Pattern.compile("");
        public String text;
    }

    public class MarkdownImage extends Syntax {
        public static final Pattern pattern = Pattern.compile("^!\\[\\]\\((\\S+)(?:\\s\"(.+)\")?\\)$");
        private Image image;
        private String hover = null;
    }

    public class Heading extends Syntax {
        public static final Pattern pattern = Pattern.compile("^(#{1,5})\\s?([\\w\\d\\s]*?)\\n");
        private String text;
        private int level;
    }

    public class List extends Syntax {
        public static final Pattern pattern = Pattern.compile("^([-\\*\\d\\.]){1,2} .+?\\n(?:\\d?\\1 .+\\n?)*");
        private String[] items;

        public class OrderedList extends List {
            public static final Pattern pattern = Pattern.compile("^\\d\\. .+?\\n(?:\\d\\. .+\\n?)*");
        }

        public class UnorderedList extends List {
            public static final Pattern pattern = Pattern.compile("^([-\\*]) .+?\n(?:\1 .+\n?)*");
        }
    }

    public class Quote extends Syntax {
        public static final Pattern pattern = Pattern.compile("^>\\s?(.+(?:.*?\\n)*)");
        private String[] text;
    }

    public class Rule extends Syntax {
        public static final Pattern pattern = Pattern.compile("^---$");
    }

    public class Table extends Syntax {
        public static final Pattern pattern = Pattern.compile("^(\\|(?:\\s*.+?\\s*\\|)+?)\\n\\|(?:\\s*\\-+?\\s*\\|)+\\n(\\|(?:\\s*.+?\\s*\\|)+\\n?)+", Pattern.MULTILINE);
        private int columns, rows;
        private String[][] table;
    }

    public class Definition extends Syntax {
        public static final Pattern pattern = Pattern.compile("^(.*?)\\n((?:\\:\\s?.*\\n?)+)");
        private String term, definition;
    }

    public class CodeBlock extends Syntax {
        public static final Pattern pattern = Pattern.compile("^```(\\w+)?\\n((?:.*\\n)*)```");
        private String language, code;
    }
    
    public class Footnote extends Syntax {
        public static final Pattern pattern = Pattern.compile("^\\[\\^(\\d|\\w+)\\]:\\s?(.*)");
        private int num;
        private String note, identifier = null;
    }

    public class Link {
        public static final Pattern pattern = Pattern.compile("\\[(.+)\\]\\(((?:https:\\/\\/)?(?:\\w+\\.)?.+\\.\\w+\\/?)(?: \"(.+)\")?\\)");
        private URL url;
        private Rectangle box;
        private String hover = null;
    }

    public static Syntax[] compile(String[] markdown) {
        LinkedList<Syntax> syntax = new LinkedList<Syntax>();


        // Iterate the syntax lines
        for (int l = 0; l < markdown.length; l++) {

        }

        Syntax[] array = new Syntax[syntax.size()];
        for (int i = 0; i < syntax.size(); i++)
            array[i] = syntax.get(i);
        return array;
    }

}
