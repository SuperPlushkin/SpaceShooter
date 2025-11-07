package edu.game;

import edu.GameScene;
import edu.IGameActionsHandler;
import edu.IShootHandler;
import edu.engine.Assets;
import edu.engine.SceneController;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;


public class Player {

    private final IShootHandler shootHandler;
    private final IGameActionsHandler actionsHandler;

    private double x;
    private double y;
    private double w = 56;
    private double h = 48;

    private double speed = 400; // пикс/сек
    private int lives = 3;
    private int hp = 3;

    private final int MAX_HP = 3;

    // стрельба
    private long lastShot = 0;
    private long fireDelay = 300_000_000L;

    // костюм
    private final Image sprite = Assets.getImage("kirill_ship.png");

    public Player(double startX, double startY, GameScene gameScene) {
        this.x = startX;
        this.y = startY;
        this.shootHandler = gameScene;
        this.actionsHandler = gameScene;
    }

    public void update (double dt, long now, edu.engine.Keys keys){
        double vx = 0, vy = 0;

        if (keys.isDown(KeyCode.A) || keys.isDown(KeyCode.LEFT)) vx -= speed;
        if (keys.isDown(KeyCode.D) || keys.isDown(KeyCode.RIGHT)) vx += speed;
        if (keys.isDown(KeyCode.W) || keys.isDown(KeyCode.UP)) vy -= speed;
        if (keys.isDown(KeyCode.S) || keys.isDown(KeyCode.DOWN)) vy += speed;

        x += vx * dt;
        y += vy * dt;

        // границы окна
        double W = SceneController.WIDTH;
        double H = SceneController.HEIGHT;

        if (x<32) x=32;
        if (x > W -32) x = W - 32;
        if (y < 80) y = 80;
        if (y < H - 80) y = H - 80;

        // стрельба
        if(keys.isDown(KeyCode.SPACE) && now - lastShot > fireDelay){
            shootHandler.makeShoot(x, y - 36, -300, true);
            lastShot = now;
        }
    }
    public void render (GraphicsContext g){
        g.drawImage(sprite, x - w / 2, y - h / 2, w, h);
    }

    public boolean checkBulletCollision(Bullet bullet){
        if (bullet.isByPlayer())
            return false;

        double bx = bullet.getX(), by = bullet.getY(), bw = bullet.getW(), bh = bullet.getH();

        boolean hit = bx < x + w && bx + bw > x &&
                      by < y + h && by + bh > y;

        if (hit){
            // пока что просто жизни отнимаю, на форме не отображаю
            minusHP(1);
            return true;
        }

        return false;
    }

    public void minusHP(int minus_hp){
        if (hp - minus_hp <= 0){
            lives--;

            // dead логика
            if (lives == 0)
            {
                actionsHandler.endGame();
            }
            else actionsHandler.restartLastLevel();

            hp = MAX_HP;
        }
        else hp -= minus_hp;
    }

    public int getLives(){return lives;}
    public int getHp(){return hp;}
}
