package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;

public class ShortAttackEnemy extends Enemy {
    public ShortAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, float attackCooldown, GameMap gameMap) {
        super(texture, x, y, width, height, speed, health, damage, new ShortAttackAI(), gameMap);
        setAttackCooldown(attackCooldown);
    }

    @Override
    public void attack(Hero hero) {
        hero.takeDamage(getDamage());
        resetAttackCooldown();
    }
}
