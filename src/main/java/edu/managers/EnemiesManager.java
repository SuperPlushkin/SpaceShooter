package edu.managers;

import edu.IShootHandler;
import edu.game.Bullet;
import edu.game.Enemy;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemiesManager implements IShootHandler {

    private final IShootHandler shootHandler;
    private final List<Enemy> enemies = new ArrayList<>();

    public EnemiesManager(IShootHandler shotHandler){
        this.shootHandler = shotHandler;
    }

    public void spawnEnemies() {
        enemies.clear();

        int row = 4;
        int cols = 5;
        double startX = 80;
        double startY = 120;
        double gapX = 90;
        double gapY = 60;

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < cols; c++) {
                enemies.add(new Enemy(startX + c * gapX, startY + r * gapY, this));
            }
        }
    }
    public void updateEnemies(double dt, double worldW) {
        for (Enemy enemy : enemies){
            enemy.update(dt, worldW);
        }
    }
    public void renderEnemies(GraphicsContext g){
        for (Enemy enemy : enemies){
            enemy.render(g);
        }
    }

    public void makeShoot(double x, double y, double vy, boolean byPlayer){
        shootHandler.makeShoot(x, y, vy, byPlayer);
    }

    public boolean checkBulletCollision(Bullet bullet){
        if (!bullet.isByPlayer())
            return false;

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            if (enemy.checkBulletCollision(bullet)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }
}
