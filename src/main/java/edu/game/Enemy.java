package edu.game;

import edu.subclasses.interfaces.IHaveSize;
import edu.subclasses.interfaces.IShootHandler;
import edu.subclasses.classes.Assets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.util.Random;

public abstract class Enemy implements IHaveSize {

    private final IShootHandler shootHandler;

    private double x;
    private double y;
    protected double w = 46;
    protected double h = 38;

    private double vx = 45; // пикс/сек вправо

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
    protected double MIN_DELAY_MS = 375; // Минимальная задержка (миллисекунды)
    protected double MAX_DELAY_MS = 1125; // Максимальная задержка (миллисекунды)
    private static final double SHOOT_CHANCE = 0.35; // шанс выстрелить
    private static final Random random = new Random();
    private long nextAttemptTime = System.currentTimeMillis() + 1500;
    private final double ENEMY_BULLET_SPEED = 350;
    private final double DIAG_SPEED = ENEMY_BULLET_SPEED * Math.cos(Math.toRadians(45));

    // костюм
    protected Image sprite = Assets.getImage("enemy_ship-standart.png");

    protected Enemy(double x, double y, IShootHandler shootHandler) {
        this.x = x;
        this.y = y;
        this.shootHandler = shootHandler;
        this.name = STAR_WARS_NAMES[random.nextInt(STAR_WARS_NAMES.length)];
        this.currentHP = MAX_HP;
    }

    public void update(double dt, double worldW, double worldH){

        // пикс/сек вниз
        double vy = 8 * (worldH / 800); // 800 пикселей бралось за стандарт экрана, поэтому вот так. 8 - просто скорость противника базовое

        x += vx * dt;
        y += vy * dt;

        // отражаемся от краёв
        double halfW = w / 2.0;
        double boundary = 20;

        if (x - halfW < boundary) {
            x = boundary + halfW;
            vx = -vx;
        }
        else if (x + halfW > worldW - boundary){
            x = worldW - boundary - halfW;
            vx = -vx;
        }

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

        int direction = random.nextInt(4);

        double vx_bullet = 0;
        double vy_bullet = switch (direction) {
            case 0 -> {
                vx_bullet = -DIAG_SPEED;
                yield DIAG_SPEED;
            }
            case 1, 2 -> {
                vx_bullet = 0;
                yield ENEMY_BULLET_SPEED;
            }
            case 3 -> {
                vx_bullet = DIAG_SPEED;
                yield DIAG_SPEED;
            }
            default -> 0;
        };

        shootHandler.makeShoot(x, y + h / 2, vx_bullet, vy_bullet, false);
    }

    public boolean checkCollisionWithObject(IHaveSize object){
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
    public boolean takeDamage(int damage) {

        if (damage == -1) return dieInstantly();

        currentHP -= damage;
        return currentHP <= 0;
    }
    public boolean dieInstantly() {
        return takeDamage(currentHP);
    }


    public double getX() { return x; }
    public double getY() { return y; }
    public double getW() { return w; }
    public double getH() { return h; }
    public double getVx() { return vx; }

    public void setX(double x) { this.x = x; }
    public void setVx(double vx) { this.vx = vx; }
}
