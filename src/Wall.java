import java.awt.*;
import java.util.ArrayList;

public class Wall extends GameObject {

    final int WIDTH;
    final int HEIGHT;

    Vector p, v, a;
    Vector sz = new Vector(100,100);

    public Wall(int WIDTH, int HEIGHT) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;

        p = new Vector(WIDTH/2, 125);
        v = new Vector(100,100);
        a = new Vector(0,0);
        sz = new Vector(75,350);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.fillRect(p.ix, p.iy, sz.ix, sz.iy);
    }


    @Override
    public void wrap(int Width, int Height) {

    }
}
