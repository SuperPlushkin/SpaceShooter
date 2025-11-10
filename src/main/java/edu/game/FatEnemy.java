package edu.game;

import edu.subclasses.interfaces.IShootHandler;
import edu.subclasses.classes.Assets;

/**
 * "Жирный" тип противника (FatEnemy).
 * HP: 6, Ширина: 92, Скорость стрельбы: в 1.5 раза быстрее.
 */
public final class FatEnemy extends Enemy {
    public FatEnemy(double x, double y, IShootHandler shootHandler) {
        super(x, y, shootHandler);

        this.w = this.w * 2;
        this.h = this.h * 2;
        this.MAX_HP = 6;
        this.currentHP = MAX_HP;
        this.MIN_DELAY_MS = this.MIN_DELAY_MS * 0.5;
        this.MAX_DELAY_MS = this.MAX_DELAY_MS * 0.5;
        this.sprite = Assets.getImage("enemy_ship-sokol.png");
    }
}
