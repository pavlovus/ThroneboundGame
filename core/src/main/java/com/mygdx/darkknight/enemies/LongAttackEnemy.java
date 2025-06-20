package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.util.List;

public class LongAttackEnemy extends Enemy {
    private Texture bulletTexture;
    private List<Bullet> bullets;
    protected boolean flip = false;
    private float damageCooldownTimer = 0f; // Таймер для захисту від повторного урону
    private static final float DAMAGE_COOLDOWN = 0.1f; // Коротший кулдаун для урону

    public LongAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, float attackCooldown, Texture bulletTexture, List<Bullet> bullets, GameMap gameMap, LongAttackAI ai) {
        super(texture, x, y, width, height, speed, health, damage, bullets, ai, gameMap, false);
        setAttackCooldown(attackCooldown);
        this.bulletTexture = bulletTexture;
        this.bullets = bullets;
    }

    @Override
    public void attack(Hero hero) {
        float angle = (float) Math.toDegrees(Math.atan2(hero.getCenterY() - getCenterY(), hero.getCenterX() - getCenterX()));
        bullets.add(new Bullet(getCenterX(), getCenterY(), angle, bulletTexture, true, this, 30, 10, 450f));
        resetAttackCooldown();
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);
        setFlip(getCenterX() < hero.getCenterX());
        damageCooldownTimer -= delta;
    }

    @Override
    public void takeDamage(int dmg) {
        if (damageCooldownTimer > 0) {
            System.out.println("LongAttackEnemy ignored damage: " + dmg + ", cooldown: " + damageCooldownTimer);
            return;
        }
        System.out.println("LongAttackEnemy took " + dmg + " damage");
        health -= dmg;
        if (health <= 0) {
            this.setDead(true);
        }
        damageIndicators.add(new DamageIndicator("-" + dmg, nextIsRight, dmg));
        nextIsRight = !nextIsRight;
        damageCooldownTimer = DAMAGE_COOLDOWN; // Використовуємо коротший кулдаун
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, getWidth()/2f, getHeight()/2f, (float) getWidth(), (float) getHeight(), 1, 1, 0f, 0, 0, texture.getWidth(), texture.getHeight(), flip, false);
        drawDamageIndicators(batch);
    }

    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean isFlip() {
        return flip;
    }
}
