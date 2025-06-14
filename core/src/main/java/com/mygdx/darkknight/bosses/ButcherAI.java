package com.mygdx.darkknight.bosses;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.enemies.EnemyAI;

public class ButcherAI implements EnemyAI {
    private Rectangle roomBounds;

    public ButcherAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
    }

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        Butcher butcher = (Butcher) self;

        if (butcher.isPreparingCharge()) {
            return; // Без руху під час підготовки
        }

        // Обчислюємо напрямок і швидкість
        Vector2 direction;
        if (butcher.isCharging()) {
            direction = butcher.getChargeDirection();
        } else {
            direction = new Vector2(hero.getCenterX() - butcher.getCenterX(), hero.getCenterY() - butcher.getCenterY()).nor();
        }

        // Виконуємо рух
        if (direction != null) {
            float moveX = direction.x * butcher.getSpeed() * delta;
            float moveY = direction.y * butcher.getSpeed() * delta;
            butcher.move(moveX, moveY);
        }

        // Обмежуємо позицію в межах кімнати
        float newX = MathUtils.clamp(butcher.getX(), roomBounds.x, roomBounds.x + roomBounds.width - butcher.getWidth());
        float newY = MathUtils.clamp(butcher.getY(), roomBounds.y, roomBounds.y + roomBounds.height - butcher.getHeight());
        butcher.setPosition(newX, newY);
    }
}
