package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.util.List;

public class ShortAttackEnemy extends Enemy {
    private static final float MAX_ATTACK_BACK_DISTANCE = 8f; // Наскільки далеко відступає
    private static final float MAX_ATTACK_FORWARD_DISTANCE = 12f;
    private static final float BACK_TIME = 0.3f;
    private static final float ATTACK_TIME = 0.15f;

    private boolean isAttacking = false;
    private boolean isMovingBack = false;
    private Vector2 attackDirection = new Vector2();
    private float attackTimer = 0f;
    private Vector2 originalPosition = new Vector2();

    private int damage = 1;
    private Hero hero;

    public ShortAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, float attackCooldown, List<Bullet> bullets, GameMap gameMap, ShortAttackAI ai) {
        super(texture, x, y, width, height, speed, health, damage, bullets, ai, gameMap, false);
        setAttackCooldown(attackCooldown);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (!isAttacking) {
            originalPosition.set(x, y);
        }
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);

        if (isAttacking) {
            handleAttackMovement(delta);
        }
    }

    @Override
    public void attack(Hero hero) {
        if (!canAttack()) return;

        attackDirection.set(
                hero.getCenterX() - getCenterX(),
                hero.getCenterY() - getCenterY()
        ).nor();

        isAttacking = true;
        isMovingBack = true;
        attackTimer = 0f;
        originalPosition.set(getX(), getY());
        resetAttackCooldown();

        this.hero = hero;
    }

    private void handleAttackMovement(float delta) {
        attackTimer += delta;

        if (isMovingBack) {
            float progress = Math.min(attackTimer / BACK_TIME, 1f);
            float currentDistance = -MAX_ATTACK_BACK_DISTANCE * progress;
            Vector2 offset = attackDirection.cpy().scl(currentDistance);
            setPosition(originalPosition.x + offset.x, originalPosition.y + offset.y);

            if (attackTimer >= BACK_TIME) {
                isMovingBack = false;
                attackTimer = 0f;
                originalPosition.set(getX(), getY()); // Точка старту атаки
            }
        } else {
            float progress = Math.min(attackTimer / ATTACK_TIME, 1f);
            float currentDistance = MAX_ATTACK_FORWARD_DISTANCE * progress;
            Vector2 offset = attackDirection.cpy().scl(currentDistance);
            setPosition(originalPosition.x + offset.x, originalPosition.y + offset.y);

            if (attackTimer >= ATTACK_TIME) {
                isAttacking = false;
                hero.takeDamage(damage, armorIgnore);
            }
        }
    }
}
