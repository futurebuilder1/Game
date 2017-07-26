import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Game extends JFrame implements KeyListener {

    //window vars
    private final int MAX_FPS; //maximum refresh rate
    private final int WIDTH; //window width
    private final int HEIGHT; //window height

    //double buffer strategy
    private BufferStrategy strategy;

    private ArrayList<Integer> keys = new ArrayList<>();

    //loop variables
    private boolean isRunning = true; //is the window running
    private long rest = 0; //how long to sleep the main thread

    //timing variables
    private float dt; //delta time
    private long lastFrame; //time since last frame
    private long startFrame; //time since start of frame
    private int fps; //current fps

    Vector p;
    Vector v;
    Vector a;

    Vector p2;
    Vector v2;
    Vector a2;

    Vector p3;
    Vector v3;
    Vector a3;

    float friction = 0.95f;
    float push;

    int sz;
    int sz2;
    int sz3;

    public Game(int width, int height, int fps){
        super("Tanks");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    /*
     * init()
     * initializes all variables needed before the window opens and refreshes
     */
    void init(){
        //initializes window size
        setBounds(0, 0, WIDTH, HEIGHT);
        setResizable(false);

        //set jframe visible
        setVisible(true);

        //set default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //create double buffer strategy
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        addKeyListener(this);
        setFocusable(true);

        //set initial lastFrame var
        lastFrame = System.currentTimeMillis();

        //set background window color
        setBackground(Color.BLACK);

        p = new Vector(WIDTH/2,HEIGHT/2);
        v = new Vector(0,0);
        a = new Vector(0,0);

        p2 = new Vector(WIDTH/2 + 150, HEIGHT/2 + 150);
        v2 = new Vector(0,0);
        a2 = new Vector(0,0);

        p3 = new Vector(WIDTH/2 - 250, HEIGHT/2 - 200);
        v3 = new Vector(0,0);
        a3 = new Vector(0,0);

        sz = 100;
        push = 1000;

        sz2 = sz;
        sz3 = sz + 100;
    }

    /*
     * update()
     * updates all relevant game variables before the frame draws
     */

    private void update(){
        //update current fps
        fps = (int)(1f/dt);

        handleKeys();

        // wall collision
        if (p.x + sz > WIDTH || p.x < 0) {
            v.setX(v.x *= -1);
            p.add(Vector.mult(v, dt));
        }
        if (p.y + sz > HEIGHT || p.y < 0) {
            v.setY(v.y *= -1);
            p.add(Vector.mult(v, dt));
        }

        if (p2.x + sz > WIDTH || p2.x < 0) {
            v2.setX(v2.x *= -1);
            p2.add(Vector.mult(v2, dt));
        }
        if (p2.y + sz > HEIGHT || p2.y < 0) {
            v2.setY(v2.y *= -1);
            p2.add(Vector.mult(v2, dt));
        }

        if (p3.x + sz > WIDTH || p3.x < 0) {
            v3.setX(v3.x *= -1);
            p3.add(Vector.mult(v3, dt));
        }
        if (p3.y + sz > HEIGHT || p3.y < 0) {
            v3.setY(v3.y *= -1);
            p3.add(Vector.mult(v3, dt));
        }

        // aabb collision detection
        if (
                // sz & sz2 = width and height
                p.x < p2.x + sz2 && // x1 min < x2 max, x1 < x2 + w2
                p.x + sz > p2.x && // x1 max > x2 min, x1 + w1 > x2
                p.y < p2.y + sz2 && // y2 min < y2 max
                p.y + sz > p2.y // y1 max > y2 min
                ) {
            // v = v * (p - p2)
            v = Vector.mult(Vector.normalize(Vector.sub(p, p2)), v.mag());
            v2 = Vector.mult(Vector.normalize(Vector.sub(p2, p)), v2.mag());
        }

        //v += a * dt;
        //p += v * dt;
        v.add(Vector.mult(a, dt));
        v.mult(friction);
        p.add(Vector.mult(v, dt));
        a = new Vector(0,0);

        v2.add(Vector.mult(a2, dt));
        v2.mult(friction);
        p2.add(Vector.mult(v2, dt));
        a2 = new Vector(0,0);

        v3.add(Vector.mult(a3, dt));
        v3.mult(friction);
        p3.add(Vector.mult(v3, dt));
        a3 = new Vector(0,0);
    }

    /*
     * draw()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
    private void draw(){
        //get canvas
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

        //clear screen
        g.clearRect(0,0, WIDTH, HEIGHT);

        //g.setColor(Color.red);
        g.setColor(new Color(0, 200, 255));
        g.fillRect(p.ix, p.iy, sz, sz);

        g.setColor(new Color(75, 255, 50));
        g.fillRect(p2.ix, p2.iy, sz2, sz2);

        g.setColor(new Color(100, 50, 250));
        g.fillRect(p3.ix, p3.iy, sz3, sz3);

        //draw fps
        g.setColor(Color.GREEN);
        g.drawString(Long.toString(fps), 10, 40);

        //release resources, show the buffer
        g.dispose();
        strategy.show();
    }

    private void handleKeys() {
        for (int i = 0; i < keys.size(); i++) {
            switch(keys.get(i)) {
                case KeyEvent.VK_W:
                    a = Vector.unit2D((float)Math.toRadians(-90));
                    a.mult(push);
                    break;
                case KeyEvent.VK_S:
                    a = Vector.unit2D((float)Math.toRadians(90));
                    a.mult(push);
                    break;
                case KeyEvent.VK_A:
                    a = Vector.unit2D((float)Math.PI);
                    a.mult(push);
                    break;
                case KeyEvent.VK_D:
                    a = Vector.unit2D(0);
                    a.mult(push);
                    break;

                case KeyEvent.VK_I:
                    a2 = Vector.unit2D((float)Math.toRadians(-90));
                    a2.mult(push);
                    break;
                case KeyEvent.VK_K:
                    a2 = Vector.unit2D((float)Math.toRadians(90));
                    a2.mult(push);
                    break;
                case KeyEvent.VK_J:
                    a2 = Vector.unit2D((float)Math.PI);
                    a2.mult(push);
                    break;
                case KeyEvent.VK_L:
                    a2 = Vector.unit2D(0);
                    a2.mult(push);
                    break;

                case KeyEvent.VK_UP:
                    a3 = Vector.unit2D((float)Math.toRadians(-90));
                    a3.mult(push);
                    break;
                case KeyEvent.VK_DOWN:
                    a3 = Vector.unit2D((float)Math.toRadians(90));
                    a3.mult(push);
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (!keys.contains(keyEvent.getKeyCode()))
            keys.add(keyEvent.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        for (int i = keys.size() - 1; i >= 0; i--) {
            if (keyEvent.getKeyCode() == keys.get(i))
                keys.remove(i);
        }
    }

    public BufferedImage createTexture(String path) {
        try {
            return ImageIO.read(new File(path));
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
         * run()
         * calls init() to initialize variables
         * loops using isRunning
            * updates all timing variables and then calls update() and draw()
            * dynamically sleeps the main thread to maintain a framerate close to target fps
         */
    public void run(){
        init();

        while(isRunning){

            //new loop, clock the start
            startFrame = System.currentTimeMillis();

            //calculate delta time
            dt = (float)(startFrame - lastFrame)/1000;

            //update lastFrame for next dt
            lastFrame = startFrame;

            //call update and draw methods
            update();
            draw();

            //dynamic thread sleep, only sleep the time we need to cap the framerate
            //rest = (max fps sleep time) - (time it took to execute this frame)
            rest = (1000/MAX_FPS) - (System.currentTimeMillis() - startFrame);
            if(rest > 0){ //if we stayed within frame "budget", sleep away the rest of it
                try{ Thread.sleep(rest); }
                catch (InterruptedException e){ e.printStackTrace(); }
            }
        }

    }

    //entry point for application
    public static void main(String[] args){
        Game game = new Game(800, 600, 60);
        game.run();
    }

}
