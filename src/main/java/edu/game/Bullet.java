package edu.game;

import edu.subclasses.Assets;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bullet {
    private double x;
    private double y;
    private double vx;
    private double vy;

    private double w = 20;
    private double h = 30;

    private final boolean byPlayer;
    private static final double OFFSCREEN_PADDING = 50.0;

    private final Image sprite_enemy_bullet = Assets.getImage("bullet.png");
    private final Image sprite_player_bullet = Assets.getImage("laser_bullet2.png");

    public Bullet(double x, double y, double vx, double vy, boolean byPlayer) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.byPlayer = byPlayer;

        if(byPlayer){
            w = 8;
            h = 23;
        }
    }

    public void update(double dt){
        y += vy * dt;
        x += vx * dt;
    }
    public void render (GraphicsContext g){
        // Вычисляем угол поворота (в радианах)
        // Math.atan2(vx, -vy) используется для получения угла,
        // где (0, 1) — это "вверх" (на -vy, так как Y инвертирован в JavaFX)
        // PS: это придумал не Кирилл

        double angleDeg = Math.toDegrees(Math.atan2(vx, -vy));

        g.save();
        g.translate(x, y);
        g.rotate(angleDeg);

        if (!byPlayer)
        {
            g.drawImage(sprite_enemy_bullet, -w / 2, -h / 2, w, h);
        }
        else g.drawImage(sprite_player_bullet, -w / 2, -h / 2, w, h);

        g.restore();
    }

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

    public boolean isByPlayer() {
        return byPlayer;
    }
    public boolean isOffscreen() {
        return y < - OFFSCREEN_PADDING || y > 580 + OFFSCREEN_PADDING ||
               x < - OFFSCREEN_PADDING || x > 520 + OFFSCREEN_PADDING; // Пока что колхоз
    }
}
