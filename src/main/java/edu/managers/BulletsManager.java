package edu.managers;

import edu.game.Bullet;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BulletsManager {

    private final List<Bullet> bullets = new ArrayList<>();

    public void updateBullets (double dt){
        Iterator<Bullet> it = bullets.iterator();
        while(it.hasNext()){
            Bullet b = it.next();
            b.update(dt);

            if (b.isOffscreen()){
                it.remove();
            }
        }
    }
    public void renderBullets (GraphicsContext g){
        for (Bullet b: bullets){
            b.render(g);
        }
    }

    public void makeBullet (double x, double y, double vx, double vy, boolean byPlayer){
        bullets.add(new Bullet(x, y, vx, vy, byPlayer));
    }
    public void clearBullets(){
        bullets.clear();
    }
    public List<Bullet> getBullets(){
        return bullets;
    }
}
