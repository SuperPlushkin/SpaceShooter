package edu.managers;

import edu.ui.GameScene;
import edu.subclasses.interfaces.IGameActionsHandler;
import edu.subclasses.interfaces.IShootHandler;
import edu.game.Bullet;
import edu.game.Level;
import edu.game.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class LevelsManager implements IShootHandler, IGameActionsHandler {

    private final GameScene gameScene;

    private final Player player;
    private final List<Level> levels = new ArrayList<>();
    private Level activeLevel;
    private int activeLevelIndex = 0;

    private AnimationTimer loop;
    private boolean paused = false; // Оставляем 'paused' для удобства в makeUpdateTimer()
    private PauseType pauseType = PauseType.None;

    private enum PauseType {
        None,               // Игра идет
        JustPause,          // Обычная пауза (по ESC)
        GameOver,           // Игра окончена (проигрыш/победа)
        ShowingLevelResults // Показ промежуточных результатов после уровня
    }

    private int totalEnemiesKilled = 0;
    private double totalGameTime = 0;
    private int levelEnemiesKilled = 0;
    private double currentLevelTime = 0;

    public LevelsManager (double startXPlayer, double startYPlayer, GraphicsContext g, GameScene gameScene) {
        this.player = new Player(startXPlayer, startYPlayer, this);
        this.gameScene = gameScene;

        initLevels(); // Создаем уровни при инициализации
        makeUpdateTimer(g);
        loop.start();
    }

    private void makeUpdateTimer (GraphicsContext g){
        loop = new AnimationTimer() {
            long prev = 0;

            @Override
            public void handle(long now) {
                if (prev == 0){
                    prev = now;
                    return;
                }
                double dt = Math.min((now - prev) / 1_000_000_000.0, 0.05);
                prev = now;

                if (!paused) {

                    var keys = gameScene.getPressedKeys();

                    totalGameTime += dt;
                    currentLevelTime += dt;

                    checkLevelWin();

                    double highestEnemyByY = activeLevel.update(dt, getW(), getH());
                    player.update(highestEnemyByY, dt, now, keys, getW(), getH() - 40); // колхоз с минус 40 убрать надо

                    checkPlayerCollision();

                    gameScene.updateStatsInfo(totalEnemiesKilled, totalGameTime);
                }

                activeLevel.render(g, getW(), getH());
                player.render(g);
            }
        };
    }

    public void startGame() {
        makePause(PauseType.None);

        player.reset();

        totalEnemiesKilled = 0;
        totalGameTime = 0;
        levelEnemiesKilled = 0;
        currentLevelTime = 0;

        resetLevels();
        activeLevelIndex = 0;
        activeLevel = levels.getFirst();

        restartActiveLevel();
    }
    public void stopGame() {
        makePause(PauseType.JustPause);
    }
    public void endGame(boolean isWin) {
        makePause(PauseType.GameOver);

        if (isWin)
        {
            gameScene.renderWinGame(getFormatedCompletedLevels(), totalGameTime, currentLevelTime);
        }
        else gameScene.renderLoseGame(getFormatedCompletedLevels(activeLevelIndex), totalGameTime, currentLevelTime);
    }

    private void initLevels() {
        levels.add(new Level(this, 2));
        levels.add(new Level(this, 1));
        levels.add(new Level(this, 3));

        activeLevel = levels.getFirst();
    }
    public void restartActiveLevel() {
        if (activeLevel != null) {
            activeLevel.clearLevel();
            activeLevel.spawnEnemies();

            if (totalEnemiesKilled != 0) {
                totalEnemiesKilled -= levelEnemiesKilled;
            }

            player.onLevelReset();

            levelEnemiesKilled = 0;
            currentLevelTime = 0;

            gameScene.clearPressedKeys();
            gameScene.updatePlayerInfo(player.getLives(), player.getHp());
            gameScene.updateStatsInfo(totalEnemiesKilled, totalGameTime);
            gameScene.updateLevelTitle(activeLevelIndex, levels.size());
        }
    }
    private void resetLevels(){
        for (Level level : levels)
            level.clearLevel();
    }

    private void makePause(PauseType newPauseType){
        pauseType = newPauseType;
        paused = newPauseType != PauseType.None;
//        updateLoopState();
    }
    public void togglePause() {
        if (pauseType == PauseType.GameOver || pauseType == PauseType.ShowingLevelResults)
            return;

        if (pauseType == PauseType.JustPause) {
            makePause(PauseType.None);
            gameScene.onPauseChange(false);
        }
        else
        {
            makePause(PauseType.JustPause);
            gameScene.onPauseChange(true);
        }
    }

    public void makeShoot(double x, double y, double vx, double vy, boolean byPlayer){
        if (activeLevel != null) {
            activeLevel.makeShoot(x, y, vx, vy, byPlayer);
        }
    }

    public void onPlayerMinusLive(){
        restartActiveLevel();
    }
    public void onPlayerDead(){
        endGame(false);
    }
    public void failLevelByEnemiesReachingBottom() {
        endGame(false);
    }
    public void onEnemyKilled() {
        totalEnemiesKilled++;
        levelEnemiesKilled++;
        gameScene.updateStatsInfo(totalEnemiesKilled, totalGameTime);
    }

    public void checkPlayerCollision() {

        // Игрок столкнулся с врагом -> Мгновенная смерть
        if (activeLevel.checkObjectCollisionOnEnemiesAndDieInstantly(player)){
            player.dieInstantly();
            return;
        }

        var bulletsLeft = activeLevel.checkBulletCollisionOnEnemies();
        var bulletsToDelete = new ArrayList<Integer>();

        for (int i = 0; i < bulletsLeft.size(); i++) {
            Bullet bullet = bulletsLeft.get(i);

            if(bullet.isByPlayer())
                continue;

            if (player.checkCollisionWithObject(bullet)) {
                bulletsToDelete.add(i);
                player.minusHP(1); // Колхоз надо убрать. Надо брать урон из пули
                gameScene.updatePlayerInfo(player.getLives(), player.getHp());
            }
        }

        for (int i = bulletsToDelete.size() - 1; i >= 0; i--) {
            int index = bulletsToDelete.get(i);
            if (index < bulletsLeft.size()){
                bulletsLeft.remove(index);
            }
        }
    }
    public void checkLevelWin() {
        if (!activeLevel.isCompleted())
            return;

        if (activeLevelIndex < levels.size() - 1)
        {
            makePause(PauseType.ShowingLevelResults);
            gameScene.renderLevelComplete(getFormatedCompletedLevels(), (activeLevelIndex + 1), totalGameTime, currentLevelTime);
        }
        else endGame(true);
    }
    public void goToNextLevel() {
        if (activeLevelIndex < levels.size() - 1) {
            activeLevelIndex++;
            activeLevel = levels.get(activeLevelIndex);

            player.reset();

            restartActiveLevel();

            makePause(PauseType.None);
        }
    }

    public String getFormatedCompletedLevels(){
        return (activeLevelIndex + 1) + "/" + levels.size();
    }
    public String getFormatedCompletedLevels(int activeLevelIndex){
        return activeLevelIndex + "/" + levels.size();
    }

    public double getW() {
        return gameScene.getW();
    }
    public double getH() {
        return gameScene.getH();
    }
}
