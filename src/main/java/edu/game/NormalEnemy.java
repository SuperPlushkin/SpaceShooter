package edu.game;

import edu.subclasses.interfaces.IShootHandler;

/**
 * Стандартный тип противника (Normal Enemy).
 * HP: 3, Ширина: 46, Скорость стрельбы: обычная.
 */
public final class NormalEnemy extends Enemy {

    public NormalEnemy(double x, double y, IShootHandler shootHandler) {
        super(x, y, shootHandler);
    }
}
