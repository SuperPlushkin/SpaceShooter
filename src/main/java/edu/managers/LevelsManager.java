package edu.managers;

import edu.subclasses.interfaces.ISoundActionsHandler;
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
import java.util.Random;

public class LevelsManager implements IShootHandler, IGameActionsHandler, ISoundActionsHandler {

    private final GameScene gameScene;
    private final SoundManager soundManager;

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

    public LevelsManager (double startXPlayer, double startYPlayer, GraphicsContext g, GameScene gameScene, SoundManager soundManager) {
        this.player = new Player(startXPlayer, startYPlayer, this);
        this.gameScene = gameScene;
        this.soundManager = soundManager;

        initLevels();
        makeUpdateTimer(g);
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
        loop.start();

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
        loop.stop();
    }
    public void endGame(boolean isWin) {
        makePause(PauseType.GameOver);

        if (isWin)
        {
            onGameOverWin(); // ЗВУК ПОБЕДЫ
            gameScene.renderWinGame(getFormatedCompletedLevels(), totalGameTime, currentLevelTime);
        }
        else
        {
            onGameOverLose(); // ЗВУК ПРОИГРЫША
            gameScene.renderLoseGame(getFormatedCompletedLevels(activeLevelIndex), totalGameTime, currentLevelTime);
        }
    }

    private void initLevels() {
        levels.add(new Level(this, this, 2));
        levels.add(new Level(this, this, 1));
        levels.add(new Level(this, this, 3));

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

            onLevelStart();
        }
    }
    private void resetLevels(){
        for (Level level : levels)
            level.clearLevel();
    }

    private void makePause(PauseType newPauseType){
        pauseType = newPauseType;
        paused = newPauseType != PauseType.None;
    }
    public void togglePause() {
        if (pauseType == PauseType.GameOver || pauseType == PauseType.ShowingLevelResults)
            return;

        if (pauseType == PauseType.JustPause) {
            makePause(PauseType.None);
            gameScene.onPauseChange(false);
            onLevelStart();
        }
        else
        {
            makePause(PauseType.JustPause);
            gameScene.onPauseChange(true);
            onPauseMenu();
        }
    }

    public void makeShoot(double x, double y, double vx, double vy, boolean byPlayer){
        if (activeLevel != null) {
            activeLevel.makeShoot(x, y, vx, vy, byPlayer);
        }
    }

    public void onPlayerMinusLive(){
        restartActiveLevel();
        onPlayerDestroyed();
    }
    public void onPlayerDead(){
        endGame(false);
        onPlayerDestroyed();
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

                if(!player.isInvulnerable() && !player.minusHP(1)) // Колхоз надо убрать. Надо брать урон из пули
                    onPlayerGetHit();

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
            onLevelComplete();
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


    @Override
    public void onPlayerShoot() { soundManager.playSFX("player_shoot"); }
    @Override
    public void onEnemyShoot() { soundManager.playSFX("enemy_shoot" + (new Random().nextInt(2) + 1)); }
    @Override
    public void onEnemyDestroyed() { soundManager.playSFX("enemy_death_" + (new Random((long)currentLevelTime).nextInt(7) + 1)); }
    @Override
    public void onPlayerDestroyed() { soundManager.playSFX("player_death"); }
    @Override
    public void onPlayerGetHit() { soundManager.playSFX("player_get_hit"); }
    @Override
    public void onEnemySayPhrase() { soundManager.playSFX(soundManager.getRandomPhraseName()); }

    @Override
    public void onGameOverLose() { soundManager.playBGM("game_over_lose.mp3", 1); }
    @Override
    public void onGameOverWin() { soundManager.playBGM("game_over_win.mp3", 1); }
    @Override
    public void onLevelComplete() { soundManager.playBGM("level_complete.wav", 1); }
    @Override
    public void onPauseMenu() { soundManager.playBGM("pause_music.mp3", 1); }
    @Override
    public void onLevelStart() { soundManager.playBGM("level_music.mp3", -1); }
}
