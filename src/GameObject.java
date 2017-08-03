import java.awt.*;

public abstract class GameObject {

    float px; //float position x
    float py; //float position y

    float vx; //velocity x
    float vy; //velocity y

    float d; //direction (rad)

    public abstract void update(float dt);
    public abstract void draw(Graphics2D g);

    public abstract void wrap(int Width, int Height);


    public void takeHit(){
        return;
    }

    //returns the position x as an int
    public int getX(){
        return (int)px;
    }

    //returns the position y as an int
    public int getY(){
        return (int)py;
    }

    boolean isColliding(Vector p, Vector sz, Vector p2, Vector sz2) {
        return p.x < p2.x + sz2.x && // x1 min < x2 max, x1 < x2 + w2
                p.x + sz.x > p2.x && // x1 max > x2 min, x1 + w1 > x2
                p.y < p2.y + sz2.y && // y2 min < y2 max
                p.y + sz.y > p2.y; // y1 max > y2 min
    }
}