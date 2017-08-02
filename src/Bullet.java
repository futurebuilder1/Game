import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet {

    Vector p, v, sz;
    float lf_mx, lf;
    BufferedImage sprite;

    public boolean isActive;

    public Bullet(Vector p, Vector v, Vector sz, float lf_mx, BufferedImage image) {
        this.p = p;
        this.v = v;
        this.sz = sz;
        this.lf_mx = lf_mx;
        this.lf = 0;
        this.sprite = image;
        this.isActive = true;
    }

    public void update(float dt) {
        p.add(Vector.mult(v, dt));
        if (lf >= lf_mx) {
            isActive = false;
        }
        else lf += dt;
    }

    public void draw(Graphics2D g) {
        g.drawImage(sprite, p.ix, p.iy, sz.ix, sz.iy, null);
    }
}
