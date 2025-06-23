package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
    private static final float MIN_DISTANCE_FOR_FLIP = 30f; // Мінімальна відстань для фліпу

    private boolean isAttacking = false;
    private boolean isMovingBack = false;
    private Vector2 attackDirection = new Vector2();
    private float attackTimer = 0f;
    private Vector2 originalPosition = new Vector2();
    private int damage = 1;
    private Hero hero;
    protected boolean flip = false; // Нове поле для фліпу текстури
    private Music deathSound = null;

    public ShortAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, float attackCooldown, List<Bullet> bullets, GameMap gameMap, ShortAttackAI ai) {
        super(texture, x, y, width, height, speed, health, damage, bullets, ai, gameMap, false);
        setAttackCooldown(attackCooldown);
    }

    public ShortAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, float attackCooldown, List<Bullet> bullets, GameMap gameMap, ShortAttackAI ai, Music sound) {
        super(texture, x, y, width, height, speed, health, damage, bullets, ai, gameMap, false);
        setAttackCooldown(attackCooldown);
        deathSound = sound;
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
        this.hero = hero;

        if (isAttacking) {
            handleAttackMovement(delta);
        } else {
            // Перевіряємо відстань до героя
            float distanceToHero = Vector2.dst(getCenterX(), getCenterY(), hero.getCenterX(), hero.getCenterY());

            // Встановлюємо фліп на основі напрямку руху, тільки якщо відстань більша за мінімальну
            if (distanceToHero > MIN_DISTANCE_FOR_FLIP && ai instanceof ShortAttackAI) {
                ShortAttackAI shortAttackAI = (ShortAttackAI) ai;
                float dx = shortAttackAI.getLastDx(); // Отримуємо dx із AI (припускаємо, що ShortAttackAI має метод getLastDx)
                if (dx != 0) {
                    setFlip(dx < 0); // Фліп, якщо рухаємося ліворуч (dx < 0)
                }
            }
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

    @Override
    public void draw(SpriteBatch batch) {
        // Використовуємо змінну flip для дзеркального відображення текстури
        batch.draw(texture, x, y, getWidth()/2f, getHeight()/2f, (float) getWidth(), (float) getHeight(), 1, 1, 0f, 0, 0, texture.getWidth(), texture.getHeight(), flip, false);
        // Малюємо індикатори шкоди
        GlyphLayout layout = new GlyphLayout();
        for (DamageIndicator indicator : damageIndicators) {
            layout.setText(damageFont, indicator.text);
            damageFont.setColor(1.0f, 1, 1, indicator.alpha);
            float textX = indicator.isRight ? x + getWidth() : x - layout.width * 2;
            textX += 13;
            float textY = y + getHeight() + indicator.yOffset;
            damageFont.draw(batch, indicator.text, textX, textY);
        }
        damageFont.setColor(1f, 1f, 1f, 1f);
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
                originalPosition.set(getX(), getY());
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

    // Нові методи для керування фліпом
    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean isFlip() {
        return flip;
    }

    @Override
    public void takeDamage(int damage) {
        super.takeDamage(damage);
        if (health <= 0) {
            if (deathSound != null) {
                deathSound.stop();
                deathSound.play();
            }
        }
    }
}
