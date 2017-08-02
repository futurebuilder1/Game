import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Character extends GameObject {
    Vector p;
    Vector v;
    Vector a;

    Vector sz = new Vector(100, 100);

    BufferedImage sprite;

    ArrayList<Bullet> bullets = new ArrayList<>();

    final int WIDTH;
    final int HEIGHT;

    float friction = 0.95f;

    public Character(int WIDTH, int HEIGHT) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;

        p = new Vector(100, 250);
        v = new Vector(100, 100);
        a = new Vector(0,0);
        sz = new Vector(50, 50);

        this.sprite = sprite;
    }

    public void fireBullet(BufferedImage bulletimage) {
        bullets.add(new Bullet(
                Vector.add(p, Vector.div( sz, 2)),
                new Vector(500,0),
                new Vector(30, 20),
                1.0f,
                bulletimage
        ));
    }

    public void update(float dt) {
        if (p.x + sz.ix > WIDTH || p.x < 0) {
            v.setX(-v.x);
        }
        if (p.y + sz.ix > HEIGHT || p.y < 0) {
            v.setY(-v.y);
        }

        v.add(Vector.mult(a, dt));
        v.mult(friction);
        p.add(Vector.mult(v, dt));

        a = new Vector(0,0);

        for (int i = bullets.size() - 1; i >= 0; i--) {
            bullets.get(i).update(dt);
            if (!bullets.get(i).isActive) {
                bullets.remove(i);
            }
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(0, 200, 255));
        g.fillRect(p.ix, p.iy, sz.ix, sz.iy);

        for ( Bullet b : bullets) b.draw(g);
    }

    @Override
    public void wrap(int Width, int Height) {

    }
}