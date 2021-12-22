package src.gpc;

import java.awt.image.BufferedImage;
import java.awt.Dimension;

public abstract class Panel {

    protected Dimension dimension;

    public Panel(Dimension dimension) {
        this.dimension = dimension;
    }

    public void tick() {
    }

    public abstract BufferedImage render();

    public BufferedImage simRender(double delta) {
        return render();
    }

}
