package edu.game;

import edu.subclasses.IGameActionsHandler;
import edu.subclasses.IShootHandler;
import edu.subclasses.Assets;
import edu.managers.LevelsManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Player {

    private final IShootHandler shootHandler;
    private final IGameActionsHandler actionsHandler;

    private final double START_X;
    private final double START_Y;

    private double x;
    private double y;
    private final double w = 50;
    private final double h = 42;

    private final int MAX_HP = 3;
    private final int MAX_LIVES = 3;
    private int hp;
    private int lives;

    final int MAX_CONSECUTIVE_SHOTS = 3; // серия выстрелов максимальная
    final long fireDelay = 200_000_000L;
    final long COOLDOWN_DURATION = 1_000_000_000L;
    private long lastShot = 0;
    private int consecutiveShots = 0; // Счетчик выстрелов в текущей серии
    private long cooldownEndTime = 0; // Наносекунда окончания кулдауна

    private final Image sprite = Assets.getImage("kirill_ship.png");

    public Player(double startX, double startY, LevelsManager levelsManager) {
        this.x = startX;
        this.y = startY;
        this.START_X = startX;
        this.START_Y = startY;
        this.hp = MAX_HP;
        this.lives = MAX_LIVES;
        this.shootHandler = levelsManager;
        this.actionsHandler = levelsManager;
    }

    public void reset() {
        this.lives = MAX_LIVES;
        onLevelReset();
    }
    public void onLevelReset() {
        this.x = START_X;
        this.y = START_Y;
        this.hp = MAX_HP;
        this.lastShot = 0;
        this.consecutiveShots = 0;
        this.cooldownEndTime = 0;
    }
    public void update (double dt, long now, edu.subclasses.Keys keys, double W, double H){

        double vx = 0, vy = 0;
        double speed = 400; // пикс/сек

        if (keys.isDown(KeyCode.A) || keys.isDown(KeyCode.LEFT))
            vx -= speed;
        if (keys.isDown(KeyCode.D) || keys.isDown(KeyCode.RIGHT))
            vx += speed;
        if (keys.isDown(KeyCode.W) || keys.isDown(KeyCode.UP))
            vy -= speed;
        if (keys.isDown(KeyCode.S) || keys.isDown(KeyCode.DOWN))
            vy += speed;

        x += vx * dt;
        y += vy * dt;

        double MAX_X = 32;
        double MAX_Y = 30;

        if (x < MAX_X)
            x = MAX_X;
        if (x > W - MAX_X)
            x = W - MAX_X;
        if (y < MAX_Y)
            y = MAX_Y;
        if (y > H - MAX_Y)
            y = H - MAX_Y;

        // логика стрельбы
        boolean canShoot = keys.isDown(KeyCode.SPACE) && now - lastShot > fireDelay;

        if(canShoot){
            shootHandler.makeShoot(x, y - 36, 0, -300, true);
            lastShot = now;
        }

        // логика стрельбы очередями (потом как-нибудь)
//        if (canShoot && now >= cooldownEndTime) {
//
//            shootHandler.makeShoot(x, y - 36, 0, -300, true);
//            lastShot = now;
//            consecutiveShots++;
//
//            if (consecutiveShots >= MAX_CONSECUTIVE_SHOTS) {
//                cooldownEndTime = now + COOLDOWN_DURATION;
//                consecutiveShots = 0;
//            }
//        }
    }
    public void render (GraphicsContext g){
        g.drawImage(sprite, x - w / 2, y - h / 2, w, h);

        // отрисовка кулдауна у оружия
        if (System.nanoTime() < cooldownEndTime) {
            g.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            g.setFill(Color.RED);
            g.setTextAlign(TextAlignment.CENTER);

            double textY = y + h / 2 + 15;
            g.fillText("RELOADING...", x, textY);
        }

        // отрисовка имени
        g.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        g.setFill(Color.DARKRED);
        g.setTextAlign(TextAlignment.CENTER);

        double textY = y - h / 2 - 5;
        g.fillText("Кирилл Вейдер", x, textY);

        // отрисовка хп противника
        g.save();
        double hpBarWidth = w * 0.8; // Ширина полоски здоровья (80% ширины корабля)
        double hpBarHeight = 3;     // Высота полоски здоровья
        double hpBarX = x - hpBarWidth / 2; // Центрируем полоску
        double hpBarY = y + h / 2 + 5;      // Чуть ниже спрайта

        g.setFill(Color.BLACK);
        g.fillRect(hpBarX, hpBarY, hpBarWidth, hpBarHeight);
        g.setFill(Color.RED);
        double currentHpWidth = hpBarWidth * ((double)hp / MAX_HP);
        g.fillRect(hpBarX, hpBarY, currentHpWidth, hpBarHeight);
        g.restore();
    }

    public boolean checkBulletCollision(Bullet bullet){
        if (bullet.isByPlayer())
            return false;

        double b_halfW = bullet.getW() / 2.0;
        double b_halfH = bullet.getH() / 2.0;
        double b_x = bullet.getX();
        double b_y = bullet.getY();

        double p_halfW = this.w / 2.0;
        double p_halfH = this.h / 2.0;

        boolean overlapX = (b_x + b_halfW >= this.x - p_halfW) && (b_x - b_halfW <= this.x + p_halfW);
        boolean overlapY = (b_y + b_halfH >= this.y - p_halfH) && (b_y- b_halfH <= this.y + p_halfH);

        return overlapX && overlapY;
    }

    public void minusHP(int minus_hp){
        if (hp - minus_hp <= 0){
            lives--;

            // dead логика
            if (lives == 0)
            {
                actionsHandler.endGame(false);
            }
            else actionsHandler.restartActiveLevel();

            hp = MAX_HP;
        }
        else hp -= minus_hp;
    }

    public int getLives(){return lives;}
    public int getHp(){return hp;}
}
