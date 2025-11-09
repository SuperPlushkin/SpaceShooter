package edu.game;

import edu.subclasses.IShootHandler;
import edu.subclasses.Assets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.Random;

public abstract class Enemy {

    private IShootHandler shootHandler;

    private double x;
    private double y;
    protected double w = 46;
    private final double h = 38;

    private double vx = 45; // пикс/сек вправо
    private final double vy = 5; // пикс/сек вниз

    protected int MAX_HP = 3; // Максимальное здоровье противника
    protected int currentHP;        // Текущее здоровье противника

    private final String name;
    private static final String[] STAR_WARS_NAMES = {
        "Люк", "Лея", "Хан", "Чубакка", "Лэндо",
        "Оби-Ван", "Йода", "Мон Мотма", "Акбар", "Ведж",
        "Энакин", "Падме", "Асока", "Кейнан", "Гера",
        "Сабин", "Зэб", "Кассиан", "Джин Эрсо", "Бейл Органа",
        "Квай-Гон", "Мейс Винду", "Пло Кун", "Эйла", "Ки-Ади",
        "Шаак Ти", "Гален Эрсо", "Бо-Катан", "Эзра", "Рей",
        "Финн", "По Дэмерон", "Роуз Тико", "Маз Каната", "Джанна",
        "Ниен Нунб", "Гарвен Дрейс", "Биггс", "Датч", "Кэнон", "Дарт Не Вейдер"
    };

    // Стрельба
    protected double MIN_DELAY_MS = 500; // Минимальная задержка (миллисекунды)
    protected double MAX_DELAY_MS = 1500; // Максимальная задержка (миллисекунды)
    private static final double SHOOT_CHANCE = 0.35; // шанс выстрелить
    private static final Random random = new Random();
    private long nextAttemptTime = System.currentTimeMillis() + 1500;
    private final double ENEMY_BULLET_SPEED = 350;
    private final double DIAG_SPEED = ENEMY_BULLET_SPEED * Math.cos(Math.toRadians(45));

    // костюм
    protected Image sprite = Assets.getImage("enemy_ship.png");

    protected Enemy(double x, double y, IShootHandler shootHandler) {
        this.x = x;
        this.y = y;
        this.shootHandler = shootHandler;
        this.name = STAR_WARS_NAMES[random.nextInt(STAR_WARS_NAMES.length)];
        this.currentHP = MAX_HP;
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

        // отрисовка имени
        g.save();
        g.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        g.setFill(Color.LIGHTSKYBLUE); // Голубой цвет
        g.setTextAlign(TextAlignment.CENTER);

        double textY = y - h / 2 - 5;
        g.fillText(this.name, x, textY);
        g.restore();

        // отрисовка хп противника
        g.save();
        double hpBarWidth = w * 0.8; // Ширина полоски здоровья (80% ширины корабля)
        double hpBarHeight = 3;     // Высота полоски здоровья
        double hpBarX = x - hpBarWidth / 2; // Центрируем полоску
        double hpBarY = y + h / 2 + 3;      // Чуть ниже спрайта

        g.setFill(Color.BLACK);
        g.fillRect(hpBarX, hpBarY, hpBarWidth, hpBarHeight);
        g.setFill(Color.RED);
        double currentHpWidth = hpBarWidth * ((double)currentHP / MAX_HP);
        g.fillRect(hpBarX, hpBarY, currentHpWidth, hpBarHeight);
        g.restore();
    }

    private void tryShoot (){
        long currentTime = System.currentTimeMillis();

        if (currentTime >= nextAttemptTime) {

            double range = MAX_DELAY_MS - MIN_DELAY_MS;
            double randomDelay = MIN_DELAY_MS + random.nextDouble() * range;

            nextAttemptTime = currentTime + (long)randomDelay;

            if (random.nextDouble() >= SHOOT_CHANCE)
                return;

            shootRandomly();
        }
    }
    public void shootRandomly() {
        double vx_bullet = 0;
        double vy_bullet = 0;

        int direction = random.nextInt(4);

        switch (direction) {
            case 0: // Вниз-влево
                vx_bullet = -DIAG_SPEED;
                vy_bullet = DIAG_SPEED;
                break;
            case 1, 2: // Прямо вниз
                vx_bullet = 0;
                vy_bullet = ENEMY_BULLET_SPEED;
                break;
            case 3: // Вниз-вправо
                vx_bullet = DIAG_SPEED;
                vy_bullet = DIAG_SPEED;
                break;
        }

        shootHandler.makeShoot(x, y + h / 2, vx_bullet, vy_bullet, false);
    }

    public boolean checkBulletCollision(Bullet bullet){
        double b_halfW = bullet.getW() / 2.0;
        double b_halfH = bullet.getH() / 2.0;
        double b_x = bullet.getX();
        double b_y = bullet.getY();

        double e_halfW = this.w / 2.0;
        double e_halfH = this.h / 2.0;

        boolean overlapX = (b_x + b_halfW >= this.x - e_halfW) && (b_x - b_halfW <= this.x + e_halfW);
        boolean overlapY = (b_y + b_halfH >= this.y - e_halfH) && (b_y- b_halfH <= this.y + e_halfH);

        return overlapX && overlapY;
    }

    public boolean takeDamage(int damage) {
        currentHP -= damage;
        return currentHP <= 0;
    }
}
