package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.util.List;

public class LongAttackEnemy extends Enemy {
    private Texture bulletTexture;
    private List<Bullet> bullets;

    public LongAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, float attackCooldown, Texture bulletTexture, List<Bullet> bullets, GameMap gameMap, LongAttackAI ai) {
        super(texture, x, y, width, height, speed, health, damage, ai, gameMap);
        setAttackCooldown(attackCooldown);
        this.bulletTexture = bulletTexture;
        this.bullets = bullets;
    }

    @Override
    public void attack(Hero hero) {
        float angle = (float) Math.toDegrees(Math.atan2(hero.getCenterY() - getCenterY(), hero.getCenterX() - getCenterX()));
        bullets.add(new Bullet(getCenterX(), getCenterY(), angle, bulletTexture, "core/assets/sparkle.png", true, this, 30, 10,450f));
        resetAttackCooldown();
    }
}
