package edu.managers;

import edu.game.FatEnemy;
import edu.game.NormalEnemy;
import edu.subclasses.ILevelActionsHandler;
import edu.subclasses.IShootHandler;
import edu.game.Bullet;
import edu.game.Enemy;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EnemiesManager implements IShootHandler {

    private final IShootHandler shootHandler;
    private final ILevelActionsHandler levelsActions;
    private final List<Enemy> enemies = new ArrayList<>();
    private final int levelId;

    public EnemiesManager(IShootHandler shotHandler, ILevelActionsHandler levelsActions, int levelId){
        this.shootHandler = shotHandler;
        this.levelsActions = levelsActions;
        this.levelId = levelId;
    }

    public void spawnEnemies() {
        clearEnemies();

        if (levelId == 1)
            spawnEnemiesVersion1();
        else if (levelId == 2)
            spawnEnemiesVersion2();
    }
    public void spawnEnemiesVersion1() {
        int row = 4;
        int cols = 5;
        double startX = 80;
        double startY = 60;
        double gapX = 70;
        double gapY = 60;

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < cols; c++) {
                enemies.add(new NormalEnemy(startX + c * gapX, startY + r * gapY, this));
            }
        }
    }
    public void spawnEnemiesVersion2() {
        int cols = 3;
        double startX = 150;
        double startY = 60;
        double gapX = 100;

        for (int c = 0; c < cols; c++) {
            enemies.add(new FatEnemy(startX + c * gapX, startY, this));
            enemies.add(new FatEnemy(startX + c * gapX, startY + 80, this));
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
    public void clearEnemies() {
        enemies.clear();
    }
    public boolean areEnemiesEmpty() {
        return enemies.isEmpty();
    }

    public void makeShoot(double x, double y, double vx, double vy, boolean byPlayer){
        shootHandler.makeShoot(x, y, vx, vy, byPlayer);
    }
    public boolean checkBulletCollision(Bullet bullet){
        if (!bullet.isByPlayer())
            return false;

        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            if (enemy.checkBulletCollision(bullet)) {

                if (enemy.takeDamage(1)) {
                    iterator.remove();
                    levelsActions.enemyKilled();
                }

                return true;
            }
        }

        return false;
    }
}
