package edu.subclasses.interfaces;

public interface ISoundActionsHandler {
    void onPlayerShoot();
    void onEnemyShoot();
    void onEnemyDestroyed();
    void onPlayerDestroyed();
    void onEnemySayPhrase();
    void onGameOverLose();
    void onGameOverWin();
    void onLevelComplete();
    void onLevelStart();
    void onPlayerGetHit();
    void onPauseMenu();
}