package edu.managers;

import edu.game.FatEnemy;
import edu.game.NormalEnemy;
import edu.game.Enemy;
import edu.subclasses.interfaces.IHaveSize;
import edu.subclasses.interfaces.ILevelActionsHandler;
import edu.subclasses.interfaces.IShootHandler;
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
        else if (levelId == 3)
            spawnEnemiesVersion3();
    }

    // функции спавна противников не сам делал, потому что это запарно, потом если захочу, сам сделаю
    public void spawnEnemiesVersion1() {
        int row = 4;
        int cols = 5;
        double startX = 80;
        double startY = 75;
        double gapX = 70;
        double gapY = 60;

        for (int r = 0; r < row; r++) {
            for (int c = 0; c < cols; c++) {
                enemies.add(new NormalEnemy(startX + c * gapX, startY + r * gapY, this));
            }
        }
    }
    public void spawnEnemiesVersion2() {
        double worldW = 600;
        double centerX = worldW / 2.0;
        double lineY1 = 75;
        double lineY2 = 135;
        double bossY = 200;

        // 1. Первая линия NormalEnemy (Щит)
        int normalCols = 6;
        double normalStartX = 40;
        double normalGapX = 90;

        for (int c = 0; c < normalCols; c++) {
            enemies.add(new NormalEnemy(normalStartX + c * normalGapX, lineY1, this));
            enemies.add(new NormalEnemy(normalStartX + c * normalGapX, lineY2, this));
        }

        // 2. Вторая линия BossEnemy (Эскорт)
        enemies.add(new FatEnemy(centerX - 120, bossY, this));
        enemies.add(new FatEnemy(centerX + 120, bossY, this));
    }
    public void spawnEnemiesVersion3() {
        double worldW = 600;
        double centerX = worldW / 2.0;

        // 1. Две линии BossEnemy в центре (тяжелое ядро)
        double bossY1 = 90;
        double bossY2 = 170;
        double bossGapX = 150;

        // Линия BossEnemy 1
        enemies.add(new FatEnemy(centerX - bossGapX, bossY1, this));
        enemies.add(new FatEnemy(centerX, bossY1, this));
        enemies.add(new FatEnemy(centerX + bossGapX, bossY1, this));

        // Линия BossEnemy 2
        enemies.add(new FatEnemy(centerX - bossGapX, bossY2, this));
        enemies.add(new FatEnemy(centerX, bossY2, this));
        enemies.add(new FatEnemy(centerX + bossGapX, bossY2, this));

        // 2. Фланговая защита из NormalEnemy (быстрый маневр)
        double flankY = 240;
        double flankX = 50;
        double flankGapX = 500; // Широко расставлены

        // Нижняя линия NormalEnemy
        enemies.add(new NormalEnemy(flankX, flankY, this));
        enemies.add(new NormalEnemy(flankX + flankGapX, flankY, this));

        // Дополнительная линия NormalEnemy, чтобы прикрыть центр снизу
        enemies.add(new NormalEnemy(centerX - 50, flankY + 50, this));
        enemies.add(new NormalEnemy(centerX + 50, flankY + 50, this));
    }

    public void updateEnemies(double dt, double worldW, double worldH) {
        for (Enemy enemy : enemies){
            enemy.update(dt, worldW, worldH);
        }

        checkEnemyCollisionsAndSeparate();
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
    public boolean checkObjectCollisionOnEnemies(IHaveSize object, int damage_on_hit){
        Iterator<Enemy> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();

            if (enemy.checkCollisionWithObject(object)) {

                if (enemy.takeDamage(damage_on_hit)) {
                    iterator.remove();
                    levelsActions.onEnemyKilled();
                }
                return true;
            }
        }
        return false;
    }
    private void checkEnemyCollisionsAndSeparate() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e1 = enemies.get(i);

            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy e2 = enemies.get(j);

                if (e1.checkCollisionWithObject(e2)) {

                    // Столкновение! Вычисляем перекрытие и корректируем позицию.
                    double dx = e1.getX() - e2.getX();
                    // Сумма полуширин
                    double requiredDist = e1.getW() / 2.0 + e2.getW() / 2.0;
                    // Вычисляем, насколько они перекрываются по X
                    double overlapX = requiredDist - Math.abs(dx);

                    if (overlapX > 0) {
                        double correction = overlapX / 2.0 + 0.1; // Небольшой зазор 0.1

                        // Двигаем объекты в разные стороны
                        if (dx > 0)
                        {
                            e1.setX(e1.getX() + correction);
                            e2.setX(e2.getX() - correction);
                        }
                        else
                        {
                            e1.setX(e1.getX() - correction);
                            e2.setX(e2.getX() + correction);
                        }

                        // Меняем направление движения (отскок)
                        e1.setVx(-e1.getVx());
                        e2.setVx(-e2.getVx());
                    }
                }
            }
        }
    }
    public boolean checkIfAnyEnemyReachedY(double defeatLineY) {
        return enemies.stream().anyMatch(enemy -> enemy.getY() + enemy.getH() / 2.0 >= defeatLineY);
    }
    public double getHighestEnemyY() {
        return enemies.isEmpty() ? -1 : enemies.stream().mapToDouble(Enemy::getY).min().getAsDouble();
    }
}
