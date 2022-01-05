package src.gpc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.io.InputStream;
import src.Window;

/**
 * A class for generating and displaying maps.
 * 
 * @author TheCommandBlock
 * @since 03/01/2021
 */
public class Map extends Panel {

    /**
     * An enum for the types of celestial bodies in the game.
     * There are the six moons on which the players have their bases:
     * <p><ul>
     * <li>Dune</li>
     * <li>Verglas</li>
     * <li>Hades</li>
     * <li>Poseidon</li>
     * <li>Divine</li>
     * <li>Zeus</li>
     * </ul></p>
     * And the planet which they are all orbiting: Saturn.
     */
    public static enum Celestial {
        DUNE, VERGLAS, HADES, POSEIDON, DIVINE, ZEUS, SATURN;

        /**
         * Get an enum constant from a string of the same name
         * 
         * @param str the string resembling a field in the enum
         * @return a pointer for an enum constant
         */
        public static Celestial assign(String str) {
            switch (str.toLowerCase()) {
                case "dune": return DUNE;
                case "verglas": return VERGLAS;
                case "hades": return HADES;
                case "poseidon": return POSEIDON;
                case "divine": return DIVINE;
                case "zeus": return ZEUS;
                case "saturn": return SATURN;
                default: return null;
            }
        }
    }

    /**
     * The maps are divided into tiles of hexagonal shape
     */
    public static enum Terra {
        SAND, ROCK, ICE;
    }

    static record Tile(Terra terra, Byte level) {
    }

    /**
     * The terrain is saved as a record containing a final two-dimensional array of enum references to {@link Map.Tile}
     */
    private record Terrain(Tile[][] tiles) {
        /**
         * This map generation algorithm generates a two-dimensional array of {@link Map.Tile}'s 
         * according to a set of rules specifically designed for the given terrain type.
         * To be more precise, it executes a remote C++ script that generates the map and then 
         * returns a stream of hexadecimal values via a command-line interface. This method then 
         * retrieves those bytes and constructs a two-dimensional array from it.
         * 
         * @param width  the width of the map in tiles
         * @param height the height of the map in tiles
         * @param type   the celestial body for which the map is to be designed
         * @return the generated map represented as a two-dimensional array of enum references
         */
        public static Tile[][] generate(int width, int height, Celestial type) {
            Tile[][] map = new Tile[width][height];
            // TODO Map Generation Algorithm
            try {
                // Executing C++ map generator
                String[] cmd = { "./src/mapgen.exe", "width:" + width, "height:" + height, "type:" + type.name() };
                Process p = Runtime.getRuntime().exec(cmd);

                // Retrieving output
                InputStream is = p.getInputStream();
                String out = "";
                int b;
                do {
                    b = is.read();
                    out += (char) b;
                } while (b != -1);
                // Loading parsing specs
                String[] content = out.split("\\R");
                HashMap<String, String> specs = new HashMap<String, String>();
                for (int i = 0; i < content.length-1; i++) {
                    String[] pair = content[i].split(":");
                    if (pair.length >= 2)
                        specs.put(pair[0], pair[1]);
                }
                // Initialising parsing constants
                final String charset = "0123456789abcdefghijklmnopqrstuvwxyz";
                final String X_SEP, Y_SEP, TERRA, LEVEL;
                if (specs.get("x_sep") != null)
                    X_SEP = specs.get("x_sep");
                else
                    X_SEP = " ";
                if (specs.get("y_sep") != null)
                    Y_SEP = specs.get("y_sep");
                else
                    Y_SEP = "\n";
                if (specs.get("terra_repr") != null)
                    TERRA = specs.get("terra_repr");
                else
                    TERRA = "2x16";
                if (specs.get("level_repr") != null)
                    LEVEL = specs.get("level_repr");
                else
                    LEVEL = "1x36";
                // Reading the map blueprint
                String[] lines = content[content.length-1].split(Y_SEP);
                final int terra_len = Integer.parseInt(TERRA.split("x")[0]), terra_dgs = Integer.parseInt(TERRA.split("x")[1]);
                final int level_len = Integer.parseInt(LEVEL.split("x")[0]), level_dgs = Integer.parseInt(LEVEL.split("x")[1]);
                int y = 0, x;
                for (String line : lines) {
                    if (line.charAt(0) == -1) break;
                    x = 0;
                    for (String chars : line.split(X_SEP)) {
                        if (chars.strip().length() == 0 || chars.length() < terra_len + level_len) continue;
                        // Resolving digits
                        int terra = 0, level = 0;
                        for (int i = 0; i < terra_len; i++)
                            terra += Math.pow(terra_dgs, terra_len - i - 1) * charset.indexOf(chars.charAt(i));
                        for (int i = 0; i < level_len; i++)
                            level += Math.pow(level_dgs, level_len - i - 1) * charset.indexOf(chars.charAt(i + terra_len));
                        map[y][x++] = new Tile(Terra.values()[terra], (byte) level);
                    }
                    y++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }
    }

    Terrain map;
    Point coordinates;
    double zoom;

    /**
     * A new map of the standard size 24x24 generated specifically for the given celestial body.
     * 
     * @param dimension the width and height not of the map but of the image render
     * @param type      an enum constant defining the moon or planet for which to design the map
     */
    public Map(Dimension dimension, Celestial type) {
        super(dimension);
        this.map = new Terrain(Terrain.generate(24, 24, type));
        this.coordinates = new Point(0, 0);
        this.zoom = 1.0;
    }

    public BufferedImage render() {
        BufferedImage img = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        int w = (int) (128 * zoom), h = (int) (64 * zoom);
        boolean eo = false;
        for (int y = 0; y < Window.HEIGHT; y += h) {
            eo = y / h % 2 == 0;
            for (int x = -w / 2 * (int) ((y / h + 1) / 2 % 3 + 2); x < Window.WIDTH; x += w / 2) {
                eo = !eo;
                if (eo)
                    y -= h/2;
                int[] xs = {x, x+w/2, x+w, x+w, x+w/2, x},
                      ys = {y, y, y+h/2, y+h, y+h, y+h/2};
                if (eo) {
                    y += h/2;
                    x += w/2;
                }
                Polygon poly = new Polygon(xs, ys, 6);
                g.setColor(new Color(190, 209, 100));
                g.fill(poly);
                g.setColor(new Color(192, 228, 18));
                g.draw(poly);
            }
        }
        return img;
    }

    public void zoomIn() {
        if (zoom < 1.0)
            zoom += 0.1;
    }

    public void zoomOut() {
        if (zoom > 0.2)
            zoom -= 0.1;
    }

}
