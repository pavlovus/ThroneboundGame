package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.effects.HealingEffect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap; // Не використовується більше, але залишимо імпорт, якщо знадобиться

public class Healer extends Enemy {
    private static final float HEAL_COOLDOWN = 3.0f; // Кулдаун зцілення (секунди)
    private static final float HEAL_RADIUS = 200f; // Радіус дії зцілення
    private static final int HEAL_AMOUNT = 1; // Кількість здоров’я для зцілення
    private static final int MAX_HEALTH = 10; // Максимальне здоров’я ворога

    private float healTimer;
    private List<Enemy> enemies; // Список ворогів для зцілення (currentWaveEnemies)
    private Rectangle roomBounds;

    private List<HealingEffect> activeHealingEffects;

    private static final float EFFECT_DURATION = 1.0f; // Тривалість ефекту зцілення (1 секунда)
    private static final float EFFECT_WIDTH = 32f; // Бажана ширина ефекту
    private static final float EFFECT_HEIGHT = 32f; // Бажана висота ефекту

    public Healer(float x, float y, GameMap gameMap, Rectangle roomBounds, List<Enemy> enemies) {
        super(Assets.healerEnemyTexture, x, y, 32, 32, 80f, 4, 0, new HealerAI(roomBounds), gameMap);
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
                        // Передаємо сам об'єкт ворога до HealingEffect
                        activeHealingEffects.add(new HealingEffect(Assets.healingEffectTexture, enemy, EFFECT_WIDTH, EFFECT_HEIGHT, EFFECT_DURATION));
                    }
                }
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Малюємо себе
        super.draw(batch);

        // Малюємо всі активні ефекти зцілення
        for (HealingEffect effect : activeHealingEffects) {
            effect.draw(batch);
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public float getCenterX() {
        return x + getWidth() / 2f;
    }

    public float getCenterY() {
        return y + getHeight() / 2f;
    }

    public float getHealRadius(){
        return HEAL_RADIUS;
    }
}
