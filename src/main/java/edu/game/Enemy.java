package edu.game;

import edu.IShootHandler;
import edu.engine.Assets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.Random;

public class Enemy {

    private IShootHandler shootHandler;

    private double x;
    private double y;
    private double w = 56;
    private double h = 48;

    private double vx = 45; // пикс/сек вправо
    private double vy = 5; // пикс/сек вниз


    // Стрельба
    private static final double MIN_DELAY_MS = 2000.0; // Минимальная задержка (миллисекунды)
    private static final double MAX_DELAY_MS = 5000.0; // Максимальная задержка (миллисекунды)
    private static final double SHOOT_CHANCE = 0.20; // 10% шанс
    private static final Random random = new Random();
    private long nextAttemptTime = System.currentTimeMillis() + 500;


    // костюм
    private final Image sprite = Assets.getImage("enemy_ship.png");


    public Enemy(double x, double y, IShootHandler shootHandler) {
        this.x = x;
        this.y = y;
        this.shootHandler = shootHandler;
    }

    public void update(double dt, double worldW){
        x += vx * dt;

        // отражаемся от краёв
        if (x < 20) {
            x = 20;
            vx = -vx;
        }
        else if (x + w > worldW - 20){
            x = worldW - 20 - w;
            vx = -vx;
        }
        // можно добавить медленное спускание
         y += vy * dt;

        tryShoot();
    }
    public void render (GraphicsContext g){
        g.drawImage(sprite, x - w / 2, y - h / 2, w, h);
    }

    private void tryShoot (){
        long currentTime = System.currentTimeMillis();

        if (currentTime >= nextAttemptTime) {

            nextAttemptTime = currentTime + calculateRandomDelayMs();

            if (random.nextDouble() >= SHOOT_CHANCE)
                return;

            shootHandler.makeShoot(x, y + 36, 100, false);
        }
    }
    public boolean checkBulletCollision(Bullet bullet){

        double bx = bullet.getX(), by = bullet.getY(), bw = bullet.getW(), bh = bullet.getH();

        boolean hit = bx < x + w && bx + bw > x &&
                by < y + h && by + bh > y;

        return hit;
    }

    private long calculateRandomDelayMs() {
        double range = MAX_DELAY_MS - MIN_DELAY_MS;
        double randomDelay = MIN_DELAY_MS + random.nextDouble() * range;
        return (long)randomDelay;
    }
}
