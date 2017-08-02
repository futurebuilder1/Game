import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    Character player1;

    // game states
    public enum GAME_STATES {
        MENU,
        GAME,
        STORE,
        SCORE
    }

    public GAME_STATES GameState;

    public static int SCORE;

    //double buffer strategy
    private BufferStrategy strategy;

    String bullet_file = "Textures\\bullet.png";
    BufferedImage bullet;

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

    JPanel Menu;
    JButton Menu_Play;
    JButton Menu_Store;
    JButton Menu_Exit;

    JPanel Store;
    JButton Store_TankTwo;
    JButton Store_TankThree;
    JButton Store_TankFour;
    JButton Store_TankFive;
    JButton Store_TankSix;
    JButton Store_Menu;

    int score = 0;
    int money = 0;

    int notUnlocked = 0;
    int unlocked = 1;
    int[] tankUnlocked = new int[6];

    private PlayerTank player;

    public Game(int width, int height, int fps){
        super("Tanks");
        this.MAX_FPS = fps;
        this.WIDTH = width;
        this.HEIGHT = height;
    }

    public BufferedImage loadTexture(String filepath) {
        try {
            return ImageIO.read(new File(filepath));
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Image loadTextureGif(String filepath) {
        try {
            return new ImageIcon(new File(filepath).toURI().toURL()).getImage();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * init()
     * initializes all variables needed before the window opens and refreshes
     */
    void init(){
        //initialize JFrame
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setBounds(0, 0, WIDTH, HEIGHT);

        lastFrame = System.currentTimeMillis();

        bullet = loadTexture(bullet_file);

        player1 = new Character(WIDTH, HEIGHT);

        // set state of tank (unlocked or locked)
        tankUnlocked[0] = unlocked;
        tankUnlocked[1] = notUnlocked;
        tankUnlocked[2] = notUnlocked;
        tankUnlocked[3] = notUnlocked;
        tankUnlocked[4] = notUnlocked;
        tankUnlocked[5] = notUnlocked;

        /* UI ELEMENTS */

        // menu
        Menu = new JPanel(new GridLayout(3, 1));
        Menu.setPreferredSize(new Dimension(WIDTH, 250));

        // play game
        Menu_Play = new JButton("Play!");
        Menu_Play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                GameState = GAME_STATES.GAME;
                Menu.setVisible(false);
            }
        });

        // go to store
        Menu_Store = new JButton("Store");
        Menu_Store.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                GameState = GAME_STATES.STORE;
                Menu.setVisible(false);
                Store.setVisible(true);
            }
        });

        // exit game
        Menu_Exit = new JButton("Exit");
        Menu_Exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });

        // add buttons
        Menu.add(Menu_Play);
        Menu.add(Menu_Store);
        Menu.add(Menu_Exit);

        // set menu visible
        Menu.setVisible(true);

        this.getContentPane().add(Menu, BorderLayout.SOUTH);
        this.pack();

        // store
        Store = new JPanel(new GridLayout(2, 3));
        Store.setPreferredSize(new Dimension(WIDTH, 250));

        // go back to main menu
        Store_TankTwo = new JButton("Tank Two - Cost: 1500 Money");
        Store_TankTwo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (money >= 1500) {
                    tankUnlocked[1] = unlocked;
                    money -= 1500;
                    Store_TankTwo.setVisible(false);
                }
            }
        });

        Store_TankThree = new JButton("Tank Three - Cost: 3000 Money");
        Store_TankThree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (money >= 3000) {
                    tankUnlocked[2] = unlocked;
                    money -= 3000;
                    Store_TankThree.setVisible(false);
                }
            }
        });

        Store_TankFour = new JButton("Tank Four - Cost: 5000 Money");
        Store_TankFour.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (money >= 5000) {
                    tankUnlocked[3] = unlocked;
                    money -= 5000;
                    Store_TankFour.setVisible(false);
                }
            }
        });

        Store_TankFive = new JButton("Tank Five - Cost: 7500 Money");
        Store_TankFive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (money >= 7500) {
                    tankUnlocked[4] = unlocked;
                    money -= 7500;
                    Store_TankFive.setVisible(false);
                }
            }
        });

        Store_TankSix = new JButton("Tank Six - Cost: 10000 Money");
        Store_TankSix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (money >= 10000) {
                    tankUnlocked[5] = unlocked;
                    money -= 10000;
                    Store_TankSix.setVisible(false);
                }
            }
        });

        Store_Menu = new JButton("Main Menu");
        Store_Menu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Store.setVisible(false);
                GameState = GAME_STATES.MENU;
                Menu.setVisible(true);
            }
        });

        // add buttons
        Store.add(Store_TankTwo);
        Store.add(Store_TankThree);
        Store.add(Store_TankFour);
        Store.add(Store_TankFive);
        Store.add(Store_Menu);
        Store.add(Store_TankSix);

        // set store invisible
        Store.setVisible(false);

        this.getContentPane().add(Store, BorderLayout.SOUTH);
        this.pack();

        //set jframe visible
        setVisible(true);

        //create double buffer strategy
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        addKeyListener(this);
        setFocusable(true);

        //set initial lastFrame var
        lastFrame = System.currentTimeMillis();

        // background
        p = new Vector(100,250);
        v = new Vector(0,0);
        a = new Vector(0,0);

        p2 = new Vector(650, 250);
        v2 = new Vector(0,0);
        a2 = new Vector(0,0);

        p3 = new Vector(400, 150);
        v3 = new Vector(0,0);
        a3 = new Vector(0,0);

        sz = 50;
        push = 350;

        sz2 = sz;
        sz3 = sz;

        GameState = GAME_STATES.MENU;
    }

    /*
     * update()
     * updates all relevant game variables before the frame draws
     */

    private void update(){
        //update current fps
        fps = (int)(1f/dt);

        switch(GameState) {
            case MENU:
                break;
            case GAME:
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
                        isColliding(p, new Vector(sz, sz), p2, new Vector(sz2, sz2))
                        ) {
                    // v = v * (p - p2)
                    v = Vector.mult(Vector.normalize(Vector.sub(p, p2)), v.mag());
                    v2 = Vector.mult(Vector.normalize(Vector.sub(p2, p)), v2.mag());
                }

                if (
                        isColliding(p, new Vector(sz, sz), p3, new Vector(sz3 + 50, sz3 + 250))
                        ) {
                    v = Vector.mult(Vector.normalize(Vector.sub(p, p3)), v.mag());
                }

                player1.update(dt);

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
                break;
            case STORE:
                break;
            case SCORE:
                break;
        }


    }

    boolean isColliding(Vector p, Vector sz, Vector p2, Vector sz2) {
        return p.x < p2.x + sz2.x && // x1 min < x2 max, x1 < x2 + w2
                p.x + sz.x > p2.x && // x1 max > x2 min, x1 + w1 > x2
                p.y < p2.y + sz2.y && // y2 min < y2 max
                p.y + sz.y > p2.y; // y1 max > y2 min
    }

    /*
     * draw()
     * gets the canvas (Graphics2D) and draws all elements
     * disposes canvas and then flips the buffer
     */
    private void draw(){
        switch(GameState) {
            case MENU:
                break;
            case GAME:
                //set background window color
                setBackground(Color.BLACK);

                //get canvas
                Graphics2D gGame = (Graphics2D) strategy.getDrawGraphics();

                //clear screen
                gGame.clearRect(0,0, WIDTH, HEIGHT);

                player1.draw(gGame);
                //g.setColor(Color.red);
                gGame.setColor(new Color(0, 200, 255));
                gGame.fillRect(p.ix, p.iy, sz, sz);

                gGame.setColor(new Color(75, 255, 50));
                gGame.fillRect(p2.ix, p2.iy, sz2, sz2);

                gGame.setColor(new Color(10, 50, 250));
                gGame.fillRect(p3.ix, p3.iy, sz3 + 50, sz3 + 250);

                //draw fps
                gGame.setColor(Color.BLACK);
                gGame.drawString(Long.toString(fps), 10, 40);

                // draw score
                gGame.setColor(Color.WHITE);
                Font myFont = new Font("Times New Roman", 1, 20);
                gGame.setFont(myFont);
                gGame.drawString("Score: " + score, 15, 50);

                // draw money
                gGame.setColor(Color.WHITE);
                gGame.setFont(myFont);
                gGame.drawString("Money: " + money, 650, 50);

                //release resources, show the buffer
                gGame.dispose();
                strategy.show();
                break;
            case STORE:
                break;
            case SCORE:
                break;
        }
    }

    public void ResetGame(){
        PlayerTank player = new PlayerTank(WIDTH / 2, HEIGHT / 2, Color.PINK);

        SCORE = 0;
    }

    private void handleKeys() {
        for (int i = 0; i < keys.size(); i++) {
            switch(keys.get(i)) {
                case KeyEvent.VK_UP:
                    player1.a = Vector.unit2D((float)Math.toRadians(-90));
                    player1.a.mult(push);
                    break;
                case KeyEvent.VK_DOWN:
                    player1.a = Vector.unit2D((float)Math.toRadians(90));
                    player1.a.mult(push);
                    break;
                case KeyEvent.VK_LEFT:
                    player1.a = Vector.unit2D((float)Math.PI);
                    player1.a.mult(push);
                    break;
                case KeyEvent.VK_RIGHT:
                    player1.a = Vector.unit2D(0);
                    player1.a.mult(push);
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
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                player1.fireBullet(bullet);
                break;
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
