package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;

public class ShortAttackEnemy extends Enemy {
    public ShortAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, float attackCooldown) {
        super(texture, x, y, width, height, speed, health, damage, new ShortAttackAI());
        setAttackCooldown(attackCooldown);
    }

    @Override
    public void attack(Hero hero) {
        hero.takeDamage(getDamage());
        resetAttackCooldown();
    }
}
