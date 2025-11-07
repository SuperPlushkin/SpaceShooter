package edu.game;

import edu.engine.Assets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Bullet {
    private double x;
    private double y;
    private double vy; // скорость по Y (отрицательное значение - вверх)

    private double w = 30;
    private double h = 40;

    private boolean byPlayer;

    private final Image sprite_enemy_bullet = Assets.getImage("bullet.png");
    private final Image sprite_player_bullet = Assets.getImage("bullet_js.png");

    public Bullet(double x, double y, double vy, boolean byPlayer) {
        this.x = x;
        this.y = y;
        this.vy = vy;
        this.byPlayer = byPlayer;
    }

    public void update(double dt){
        y += vy * dt;
    }
    public void render (GraphicsContext g){
        if (!byPlayer)
        {
            g.save();
            g.translate(x - 15, y);
            g.rotate(180);
            g.drawImage(sprite_enemy_bullet, - w / 2, - h / 2, w, h);
            g.restore();
        }
        else g.drawImage(sprite_player_bullet, x - w / 2, y - h / 2, w, h);
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
    public boolean isOffscreen(){
        return y < -20;
    }
}
