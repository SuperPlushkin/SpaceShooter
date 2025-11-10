package edu.subclasses.interfaces;

public interface IGameActionsHandler {
    void onPlayerMinusLive();
    void onPlayerDead();
    void failLevelByEnemiesReachingBottom();
    void onEnemyKilled();
}
