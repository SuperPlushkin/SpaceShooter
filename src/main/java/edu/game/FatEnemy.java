package edu.game;

import edu.subclasses.IShootHandler;
import edu.subclasses.Assets;

/**
 * "Жирный" тип противника (FatEnemy).
 * HP: 6, Ширина: 92, Скорость стрельбы: в 1.5 раза быстрее.
 */
public class FatEnemy extends Enemy {
    public FatEnemy(double x, double y, IShootHandler shootHandler) {
        super(x, y, shootHandler);

        this.w = 92;
        this.MAX_HP = 6;
        this.currentHP = MAX_HP;
        this.MIN_DELAY_MS = this.MIN_DELAY_MS * 0.5;
        this.MAX_DELAY_MS = this.MAX_DELAY_MS * 0.5;
        this.sprite = Assets.getImage("enemy_ship.png"); // временно обычный спрайт
    }
}
