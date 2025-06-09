package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

public class Teleporter extends Enemy {
    private static final float TELEPORT_COOLDOWN = 3.0f; // Час між телепортаціями
    private static final float ATTACK_RANGE = 40f; // Відстань атаки
    private static final float TELEPORT_DISTANCE = 150f; // Відстань телепортації
    private static final float FADE_DURATION = 0.5f; // Тривалість ефекту зникнення/появи

    private float teleportTimer; // Таймер телепортації
    private float fadeTimer; // Таймер ефекту зникнення/появи
    private boolean isFadingOut; // Чи зникає ворог
    private boolean isTeleporting; // Чи в процесі телепортації
    private Vector2 teleportTarget; // Цільова позиція телепортації
    private Texture teleportEffectTexture; // Текстура ефекту телепортації

    public Teleporter(float x, float y, GameMap gameMap, Rectangle roomBounds) {
        // Телепортер має 3 HP, високу швидкість і 1 шкоди
        super(Assets.teleporterTexture, x, y, 32, 32, 200f, 3, 1, new TeleporterAI(roomBounds), gameMap);

        this.teleportTimer = MathUtils.random(1.0f, TELEPORT_COOLDOWN);
        this.fadeTimer = 0;
        this.isFadingOut = false;
        this.isTeleporting = false;
        this.teleportTarget = new Vector2();
        this.teleportEffectTexture = Assets.teleportEffectTexture;

        // Встановлюємо перезарядку атаки
        setAttackCooldown(0.8f);
    }

    @Override
    public void update(Hero hero, float delta) {
        if (isTeleporting) {
            // Обробка процесу телепортації
            updateTeleportation(delta);
            return;
        }

        // Оновлюємо таймер телепортації
        teleportTimer -= delta;

        if (teleportTimer <= 0) {
            // Час телепортуватися
            startTeleportation(hero);
            teleportTimer = TELEPORT_COOLDOWN;
        } else {
            // Звичайне оновлення
            super.update(hero, delta);
        }
    }

    private void startTeleportation(Hero hero) {
        isTeleporting = true;
        isFadingOut = true;
        fadeTimer = FADE_DURATION;

        // Знаходимо цільову позицію телепортації
        findTeleportTarget(hero);
    }

    private void findTeleportTarget(Hero hero) {
        // Спробуємо знайти валідну позицію для телепортації
        for (int attempts = 0; attempts < 30; attempts++) { // Збільшуємо кількість спроб
            // Випадковий кут
            float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;

            // Випадкова відстань
            float distance = MathUtils.random(TELEPORT_DISTANCE * 0.5f, TELEPORT_DISTANCE);

            // Обчислюємо нову позицію
            float targetX = hero.getCenterX() + MathUtils.cos(angle) * distance;
            float targetY = hero.getCenterY() + MathUtils.sin(angle) * distance;

            // Перевіряємо, чи позиція в межах кімнати і не заблокована
            Rectangle testRect = new Rectangle(
                targetX - getWidth() / 2,
                targetY - getHeight() / 2,
                getWidth(),
                getHeight()
            );

            // Додаткова перевірка на лінію видимості до гравця
            if (!getGameMap().isCellBlocked(testRect)) {
                // Перевіряємо, чи є лінія видимості до гравця
                Vector2 testPos = new Vector2(targetX, targetY);
                Vector2 heroPos = new Vector2(hero.getCenterX(), hero.getCenterY());

                if (getGameMap().hasLineOfSight(testPos, heroPos)) {
                    teleportTarget.set(targetX - getWidth() / 2, targetY - getHeight() / 2);
                    return;
                }
            }
        }

        // Якщо не знайшли валідну позицію з лінією видимості, шукаємо будь-яку вільну позицію
        for (int attempts = 0; attempts < 20; attempts++) {
            float angle = MathUtils.random(0, 360) * MathUtils.degreesToRadians;
            float distance = MathUtils.random(TELEPORT_DISTANCE * 0.3f, TELEPORT_DISTANCE * 0.7f);

            float targetX = hero.getCenterX() + MathUtils.cos(angle) * distance;
            float targetY = hero.getCenterY() + MathUtils.sin(angle) * distance;

            Rectangle testRect = new Rectangle(
                targetX - getWidth() / 2,
                targetY - getHeight() / 2,
                getWidth(),
                getHeight()
            );

            if (!getGameMap().isCellBlocked(testRect)) {
                teleportTarget.set(targetX - getWidth() / 2, targetY - getHeight() / 2);
                return;
            }
        }

        // Якщо все ще не знайшли, телепортуємося на поточну позицію
        teleportTarget.set(getX(), getY());
    }

    private void updateTeleportation(float delta) {
        fadeTimer -= delta;

        if (fadeTimer <= 0) {
            if (isFadingOut) {
                // Завершили зникнення, телепортуємося і починаємо з'являтися
                setPosition(teleportTarget.x, teleportTarget.y);
                isFadingOut = false;
                fadeTimer = FADE_DURATION;
            } else {
                // Завершили появу, повертаємося до звичайного стану
                isTeleporting = false;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isTeleporting) {
            // Малюємо ефект телепортації
            float alpha;
            if (isFadingOut) {
                alpha = fadeTimer / FADE_DURATION; // Зникаємо
            } else {
                alpha = 1.0f - fadeTimer / FADE_DURATION; // З'являємося
            }

            // Малюємо ворога з прозорістю
            Color oldColor = batch.getColor().cpy();
            batch.setColor(1, 1, 1, alpha);
            batch.draw(getTexture(), getX(), getY(), getWidth(), getHeight());

            // Малюємо ефект телепортації
            float effectAlpha = 1.0f - Math.abs(2 * alpha - 1); // Максимальна яскравість посередині анімації
            batch.setColor(0.5f, 0.8f, 1.0f, effectAlpha);

            float effectSize = getWidth() * 1.5f;
            float effectX = getCenterX() - effectSize / 2;
            float effectY = getCenterY() - effectSize / 2;

            batch.draw(teleportEffectTexture, effectX, effectY, effectSize, effectSize);

            batch.setColor(oldColor);
        } else {
            // Звичайне малювання
            super.draw(batch);
        }
    }

    @Override
    public void attack(Hero hero) {
        // Атакуємо гравця
        hero.takeDamage(getDamage());
        resetAttackCooldown();

        // Після атаки є шанс одразу телепортуватися
        if (MathUtils.randomBoolean(0.3f)) {
            teleportTimer = 0;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        // Не потрібно викликати dispose() для teleportEffectTexture,
        // оскільки ця текстура керується класом Assets
    }

    private Texture getTexture() {
        // Отримуємо текстуру з базового класу
        return super.texture;
    }
}
