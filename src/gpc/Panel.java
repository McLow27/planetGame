package src.gpc;

import java.awt.image.BufferedImage;
import java.awt.Dimension;

public abstract class Panel {

    Dimension dimension;

    public Panel(Dimension dimension) {
        this.dimension = dimension;
    }

    public void tick() {
    }

    public abstract BufferedImage render();

    public abstract BufferedImage simRender(double delta);

}
