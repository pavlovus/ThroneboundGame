package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;

public class ShortAttackEnemy extends Enemy {
    public ShortAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, float attackCooldown) {
        super(texture, x, y, width, height, speed, health, new ShortAttackAI());
        setAttackCooldown(attackCooldown);
    }

    @Override
    public void attack(Hero hero) {
        // Наприклад, просто наносить шкоду герою (тут треба реалізувати метод takeDamage у Hero)
        // hero.takeDamage(10);
        resetAttackCooldown();
    }
}
