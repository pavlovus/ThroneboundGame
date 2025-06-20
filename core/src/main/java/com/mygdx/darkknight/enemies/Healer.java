package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.effects.HealingEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Healer extends Enemy {
    private static final float HEAL_COOLDOWN = 3.0f;
    private static final float HEAL_RADIUS = 200f;
    private static final int HEAL_AMOUNT = 1;
    private static final int MAX_HEALTH = 10;

    private float healTimer;
    private List<Enemy> enemies;
    private Rectangle roomBounds;
    private List<HealingEffect> activeHealingEffects;
    private static final float EFFECT_DURATION = 1.0f;
    private static final float EFFECT_WIDTH = 32f;
    private static final float EFFECT_HEIGHT = 32f;
    protected boolean flip = false; // Нове поле для фліпу текстури

    public Healer(Texture texture, float x, float y, GameMap gameMap, Rectangle roomBounds, List<Enemy> enemies, List<Bullet> bullets) {
        super(texture, x, y, 40, 40, 100f, 4, 0, bullets, new HealerAI(roomBounds), gameMap, false);
        this.healTimer = 0f;
        this.enemies = enemies;
        this.roomBounds = roomBounds;
        this.activeHealingEffects = new ArrayList<>();
    }

    @Override
    public void attack(Hero hero) {
        // Хілер не атакує героя
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);
        // Встановлюємо фліп на основі позиції героя
        setFlip(getCenterX() < hero.getCenterX()); // Фліп, якщо герой праворуч
        healTimer -= delta;
        if (healTimer <= 0) {
            healNearbyEnemies();
            healTimer = HEAL_COOLDOWN;
        }

        Iterator<HealingEffect> iterator = activeHealingEffects.iterator();
        while (iterator.hasNext()) {
            HealingEffect effect = iterator.next();
            effect.update(delta);
            if (effect.isFinished()) {
                iterator.remove();
            }
        }
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
        // Малюємо всі активні ефекти зцілення
        for (HealingEffect effect : activeHealingEffects) {
            effect.draw(batch);
        }
    }

    private void healNearbyEnemies() {
        Vector2 healerPos = new Vector2(getCenterX(), getCenterY());
        for (Enemy enemy : enemies) {
            if (enemy != this && !enemy.isDead()) {
                Vector2 enemyPos = new Vector2(enemy.getCenterX(), enemy.getCenterY());
                float distance = healerPos.dst(enemyPos);
                if (distance <= HEAL_RADIUS) {
                    int currentHealth = enemy.getHealth();
                    int newHealth = Math.min(currentHealth + HEAL_AMOUNT, MAX_HEALTH);
                    if (currentHealth < newHealth) {
                        enemy.setHealth(newHealth);
                        activeHealingEffects.add(new HealingEffect(Assets.healingEffectTexture, enemy, EFFECT_WIDTH, EFFECT_HEIGHT, EFFECT_DURATION));
                    }
                }
            }
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public float getHealRadius() {
        return HEAL_RADIUS;
    }

    // Нові методи для керування фліпом
    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean isFlip() {
        return flip;
    }
}
