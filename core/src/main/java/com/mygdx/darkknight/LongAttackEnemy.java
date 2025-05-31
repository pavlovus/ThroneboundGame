package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;

public class LongAttackEnemy extends Enemy {
    private Texture bulletTexture;
    private java.util.List<Bullet> bullets;

    public LongAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, float attackCooldown, Texture bulletTexture, java.util.List<Bullet> bullets) {
        super(texture, x, y, width, height, speed, health, new LongAttackAI());
        setAttackCooldown(attackCooldown);
        this.bulletTexture = bulletTexture;
        this.bullets = bullets;
    }

    @Override
    public void attack(Hero hero) {
        float angle = (float) Math.toDegrees(Math.atan2(hero.getCenterY() - getCenterY(), hero.getCenterX() - getCenterX()));
        bullets.add(new Bullet(getCenterX(), getCenterY(), angle, bulletTexture));
        resetAttackCooldown();
    }
}
