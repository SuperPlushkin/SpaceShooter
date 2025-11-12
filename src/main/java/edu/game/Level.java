package edu.game;

import edu.subclasses.classes.Assets;
import edu.subclasses.interfaces.*;
import edu.managers.BulletsManager;
import edu.managers.EnemiesManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;

public class Level implements IShootHandler, ILevelActionsHandler {

    private final IGameActionsHandler gameActions;
    private final ISoundActionsHandler soundHandler;
    private final EnemiesManager enemiesManager;
    private final BulletsManager bulletsManager;

    private double defeatLineY = 0;
    private final double percentOfDefeatLineY = 0.75;

    protected Image sprite = Assets.getImage("background_stars-for-wars.jpg");

    public Level (IGameActionsHandler gameActions, ISoundActionsHandler soundHandler, int enemySeed) {
        this.gameActions = gameActions;
        this.soundHandler = soundHandler;
        this.enemiesManager = new EnemiesManager(this, this, soundHandler, enemySeed);
        this.bulletsManager = new BulletsManager();
    }

    public void spawnEnemies(){
        enemiesManager.spawnEnemies();
    }
    public void clearLevel(){
        bulletsManager.clearBullets();
        enemiesManager.clearEnemies();
    }

    public double update(double dt, double worldW, double worldH) {

        if (defeatLineY == 0 || defeatLineY != worldH * percentOfDefeatLineY) {
            this.defeatLineY = worldH * percentOfDefeatLineY;
        }

        enemiesManager.updateEnemies(dt, worldW, worldH);
        bulletsManager.updateBullets(dt, worldW, worldH);

        checkBulletCollisionOnEnemies();

        // СТОЛКНОВЕНИЕ С ПОЛОМ
        if (enemiesManager.checkIfAnyEnemyReachedY(defeatLineY))
            gameActions.failLevelByEnemiesReachingBottom();

        return enemiesManager.getHighestEnemyY();
    }
    public void render (GraphicsContext g, double worldW, double worldH){
        g.setFill(Color.WHITE);
        g.fillRect(0, 0, 5000, 5000);

        g.drawImage(sprite, 0, 35, worldW, worldH - 35);

        enemiesManager.renderEnemies(g);
        bulletsManager.renderBullets(g);

        // РИСОВАНИЕ ЛИНИИ ПОРАЖЕНИЯ
        g.save();
        g.setGlobalAlpha(0.4);
        g.setStroke(Color.WHITE);
        g.setLineWidth(2);
        g.setLineDashes(10, 5);
        g.strokeLine(0, defeatLineY, worldW, defeatLineY);
        g.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));
        g.setFill(Color.WHITE);
        g.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
        g.fillText("Линия поражения", worldW / 2, defeatLineY - 10);
        g.restore();
    }

    public void makeShoot(double x, double y, double vx, double vy, boolean byPlayer){
        bulletsManager.makeBullet(x, y, vx, vy, byPlayer);

        if (byPlayer)
        {
            soundHandler.onPlayerShoot(); // Звук выстрела игрока
        }
        else soundHandler.onEnemyShoot(); // Звук выстрела противника
    }
    public List<Bullet> checkBulletCollisionOnEnemies() {
        var bullets = bulletsManager.getBullets();
        var bulletsToDelete = new ArrayList<Integer>();

        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);

            if (!bullet.isByPlayer())
                continue;

            if (enemiesManager.checkObjectCollisionOnEnemies(bullet, 1))
                bulletsToDelete.add(i);
        }

        for (int i = bulletsToDelete.size() - 1; i >= 0; i--) {
            bullets.remove((int)bulletsToDelete.get(i));
        }

        return bullets;
    }
    public boolean checkObjectCollisionOnEnemiesAndDieInstantly(IHaveSize object) {
        return enemiesManager.checkObjectCollisionOnEnemies(object, -1);
    }

    public boolean isCompleted() {
        return enemiesManager.areEnemiesEmpty();
    }
    public void onEnemyKilled(){
        gameActions.onEnemyKilled();
    }
}
