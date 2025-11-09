package edu.game;

import edu.subclasses.ILevelActionsHandler;
import edu.subclasses.IShootHandler;
import edu.managers.BulletsManager;
import edu.managers.EnemiesManager;
import edu.managers.LevelsManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Level implements IShootHandler, ILevelActionsHandler {

    private final LevelsManager levelsManager;
    private final EnemiesManager enemiesManager;
    private final BulletsManager bulletsManager;

    public Level (LevelsManager levelsManager, int enemySeed) {
        this.levelsManager = levelsManager;
        enemiesManager = new EnemiesManager(this, this, enemySeed);
        bulletsManager = new BulletsManager();
    }

    public void spawnEnemies(){
        enemiesManager.spawnEnemies();
    }
    public void clearLevel(){
        bulletsManager.clearBullets();
        enemiesManager.clearEnemies();
    }

    public void update(double dt, double worldW){
        enemiesManager.updateEnemies(dt, worldW);
        bulletsManager.updateBullets(dt);

        checkBulletCollision();

        levelsManager.checkLevelWin();
    }
    public void render (GraphicsContext g, double worldW, double worldH){
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, worldW, worldH);

        enemiesManager.renderEnemies(g);
        bulletsManager.renderBullets(g);
    }

    public void makeShoot(double x, double y, double vx, double vy, boolean byPlayer){
        bulletsManager.makeBullet(x, y, vx, vy, byPlayer);
    }
    public List<Bullet> checkBulletCollision() {
        var bullets = bulletsManager.getBullets();
        var bulletsToDelete = new ArrayList<Integer>();

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);

            if (enemiesManager.checkBulletCollision(bullet))
                bulletsToDelete.add(i);
        }

        for (int i = bulletsToDelete.size() - 1; i >= 0; i--) {
            int index = bulletsToDelete.get(i);
            if (index < bullets.size()){
                bullets.remove(index);
            }
        }

        return bullets;
    }
    public boolean isCompleted() {
        return enemiesManager.areEnemiesEmpty();
    }
    public void enemyKilled(){
        levelsManager.enemyKilled();
    }
}
