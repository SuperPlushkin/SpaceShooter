package edu.game;

import edu.subclasses.classes.Keys;
import edu.subclasses.interfaces.IGameActionsHandler;
import edu.subclasses.interfaces.IHaveSize;
import edu.subclasses.interfaces.IShootHandler;
import edu.subclasses.classes.Assets;
import edu.managers.LevelsManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Player implements IHaveSize {

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

    final long fireDelay = 200_000_000L;
    private long lastShot = 0;


    // Система неуязвимости
    private boolean isInvulnerable = false;
    private long invulnerabilityStartTime = 0;
    private final long INVULNERABILITY_DURATION = 1_000_000_000L;

    // Система мигания
    private boolean isVisible = true;
    private final long BLINK_INTERVAL = 100_000_000L;
    private long lastBlinkTime = 0;

    private final Image sprite = Assets.getImage("player_ship.png");

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
        this.isInvulnerable = false;
        this.isVisible = true;
        this.invulnerabilityStartTime = 0;
        this.lastBlinkTime = 0;
    }
    public void update(double dynamicMaxY, double dt, long now, Keys keys, double W, double H){

        updateInvulnerability(now);

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

        if (dynamicMaxY == -1)
            dynamicMaxY = H;

        if (y < dynamicMaxY)
            y = dynamicMaxY;

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
    private void updateInvulnerability(long now) {
        if (!isInvulnerable)
            return;

        if (now - invulnerabilityStartTime >= INVULNERABILITY_DURATION)
        {
            isInvulnerable = false;
            isVisible = true;
        }
        else if (now - lastBlinkTime >= BLINK_INTERVAL)
        {
            isVisible = !isVisible;
            lastBlinkTime = now;
        }
    }

    public void render (GraphicsContext g){

        // отрисовка кулдауна у оружия
//        if (System.nanoTime() < cooldownEndTime) {
//            g.setFont(Font.font("Arial", FontWeight.BOLD, 10));
//            g.setFill(Color.RED);
//            g.setTextAlign(TextAlignment.CENTER);
//
//            double textY = y + h / 2 + 15;
//            g.fillText("RELOADING...", x, textY);
//        }

        // отрисовка игрока
        if (isVisible) {
            g.drawImage(sprite, x - w / 2, y - h / 2, w, h);
        }

        // отрисовка имени
        if (isVisible) {
            g.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            g.setFill(Color.GOLD);
            g.setTextAlign(TextAlignment.CENTER);

            double textY = y - h / 2 - 5;
            g.fillText("Кирилл Вейдер", x, textY);
        }

        // отрисовка хп
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

        // Отображаем индикатор неуязвимости
        if (isInvulnerable) {
            g.setFill(Color.CYAN);
            g.setFont(Font.font("Arial", FontWeight.BOLD, 8));
            g.setTextAlign(TextAlignment.CENTER);
            g.fillText("НЕУЯЗВИМ!", x, hpBarY + 15);
        }

        g.restore();
    }

    public boolean checkCollisionWithObject(IHaveSize object){

        if (isInvulnerable)
            return false;

        double obj_halfW = object.getW() / 2.0;
        double obj_halfH = object.getH() / 2.0;
        double obj_x = object.getX();
        double obj_y = object.getY();

        double halfW = this.w / 2.0;
        double halfH = this.h / 2.0;

        boolean overlapX = (obj_x + obj_halfW >= this.x - halfW) && (obj_x - obj_halfW <= this.x + halfW);
        boolean overlapY = (obj_y + obj_halfH >= this.y - halfH) && (obj_y- obj_halfH <= this.y + halfH);

        return overlapX && overlapY;
    }

    private void activateInvulnerability() {
        this.isInvulnerable = true;
        this.invulnerabilityStartTime = System.nanoTime();
        this.isVisible = true; // Начинаем с видимого состояния
        this.lastBlinkTime = System.nanoTime();
    }

    public boolean minusHP(int minus_hp){

        if (hp - minus_hp <= 0){
            lives--;

            // dead логика
            if (lives == 0)
            {
                actionsHandler.onPlayerDead();
                hp = 0;
            }
            else
            {
                actionsHandler.onPlayerMinusLive();
                hp = MAX_HP;
                return true;
            }
        }
        else
        {
            hp -= minus_hp;
            activateInvulnerability();
        }

        return false;
    }
    public boolean dieInstantly() {
        return minusHP(hp);
    }

    public boolean isInvulnerable(){return isInvulnerable;}
    public int getLives(){return lives;}
    public int getHp(){return hp;}

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getW() {
        return w;
    }
    public double getH() {
        return h;
    }
}
