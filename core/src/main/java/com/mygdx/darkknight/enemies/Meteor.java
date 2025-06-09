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
import com.mygdx.darkknight.effects.Poison;

public class Meteor extends Enemy {
    private static final float FALL_SPEED = 300f; // Швидкість падіння метеорита
    private static final float WARNING_TIME = 2.0f; // Час попередження перед падінням
    private static final float FIRE_DURATION = 5.0f; // Тривалість вогню після падіння

    private float warningTimer; // Таймер попередження
    private float fireTimer; // Таймер вогню
    private boolean hasLanded; // Чи приземлився метеорит
    private boolean hasExploded; // Чи вибухнув метеорит
    private Vector2 targetPosition; // Цільова позиція падіння
    private float startY; // Початкова висота
    private Texture warningTexture; // Текстура попередження
    private Texture fireTexture; // Текстура вогню
    private float scale; // Розмір метеорита (випадковий)
    private float alpha; // Прозорість попередження

    public Meteor(float targetX, float targetY, GameMap gameMap, Rectangle roomBounds) {
        super(Assets.meteorTexture, targetX, targetY + 500, 48, 48, FALL_SPEED, 1, 2, new MeteorAI(roomBounds), gameMap);

        // Перевіряємо, чи цільова позиція не в стіні
        Rectangle testRect = new Rectangle(targetX - 24, targetY - 24, 48, 48);
        if (gameMap.isCellBlocked(testRect)) {
            // Якщо цільова позиція в стіні, знаходимо найближчу вільну позицію
            findNearestFreePosition(targetX, targetY, gameMap);
        } else {
            this.targetPosition = new Vector2(targetX, targetY);
        }

        this.startY = getY();
        this.warningTimer = WARNING_TIME;
        this.fireTimer = FIRE_DURATION;
        this.hasLanded = false;
        this.hasExploded = false;
        this.warningTexture = Assets.meteorWarningTexture;
        this.fireTexture = Assets.meteorFireTexture;
        this.scale = MathUtils.random(0.8f, 1.2f); // Випадковий розмір
        this.alpha = 0.0f;
    }

    private void findNearestFreePosition(float x, float y, GameMap gameMap) {
        // Спіральний пошук вільної позиції
        int maxRadius = 200; // Максимальний радіус пошуку
        int step = 20; // Крок пошуку

        for (int radius = step; radius <= maxRadius; radius += step) {
            for (int angle = 0; angle < 360; angle += 30) { // Перевіряємо кожні 30 градусів
                float radians = angle * MathUtils.degreesToRadians;
                float testX = x + radius * MathUtils.cos(radians);
                float testY = y + radius * MathUtils.sin(radians);

                Rectangle testRect = new Rectangle(testX - 24, testY - 24, 48, 48);
                if (!gameMap.isCellBlocked(testRect)) {
                    this.targetPosition = new Vector2(testX, testY);
                    return;
                }
            }
        }

        // Якщо не знайшли вільну позицію, використовуємо початкову
        this.targetPosition = new Vector2(x, y);
    }

    @Override
    public void update(Hero hero, float delta) {
        if (!hasLanded) {
            // Фаза попередження
            if (warningTimer > 0) {
                warningTimer -= delta;
                // Пульсуюча прозорість для попередження
                alpha = 0.5f + 0.5f * MathUtils.sin(warningTimer * 5);

                // Коли попередження закінчується, починаємо падіння
                if (warningTimer <= 0) {
                    alpha = 1.0f;
                }
            } else {
                // Фаза падіння
                float progress = 1.0f - (getY() - targetPosition.y) / (startY - targetPosition.y);

                // Рухаємося вниз
                move(0, -getSpeed() * delta);

                // Перевіряємо, чи досягли цільової позиції
                if (getY() <= targetPosition.y) {
                    hasLanded = true;
                    explode(hero);
                }
            }
        } else if (!hasExploded) {
            // Вибух при приземленні
            explode(hero);
        } else {
            // Фаза вогню після вибуху
            fireTimer -= delta;

            // Перевіряємо, чи гравець знаходиться у вогні
            if (hero.getBoundingRectangle().overlaps(getBoundingRectangle())) {
                // Завдаємо шкоди кожну секунду
                if (MathUtils.random(0, 100) < 2) { // 2% шанс кожен кадр (приблизно раз на секунду)
                    hero.takeDamage(1);
                    hero.addEffect(new Poison(3.0f, 1, 1.0f, new Texture(Gdx.files.internal("poison.png"))));
                }
            }

            // Коли вогонь згасає, метеорит зникає
            if (fireTimer <= 0) {
                takeDamage(1); // Знищуємо метеорит
            }
        }
    }

    private void explode(Hero hero) {
        hasExploded = true;

        // Перевіряємо, чи гравець знаходиться в зоні вибуху
        float explosionRadius = getWidth() * scale * 1.5f;
        float distToHero = Vector2.dst(getCenterX(), getCenterY(), hero.getCenterX(), hero.getCenterY());

        if (distToHero < explosionRadius) {
            hero.takeDamage(getDamage());
            // Додаємо ефект отрути при попаданні вибуху
            hero.addEffect(new Poison(5.0f, 1, 1.0f, new Texture(Gdx.files.internal("poison.png"))));
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!hasLanded) {
            if (warningTimer > 0) {
                // Малюємо попередження
                Color oldColor = batch.getColor().cpy();
                batch.setColor(1, 0, 0, alpha);
                batch.draw(warningTexture,
                          targetPosition.x - warningTexture.getWidth() / 2,
                          targetPosition.y - warningTexture.getHeight() / 2);
                batch.setColor(oldColor);
            } else {
                // Малюємо падаючий метеорит
                batch.draw(getTexture(),
                          getX() - getWidth() * (scale - 1) / 2,
                          getY() - getHeight() * (scale - 1) / 2,
                          getWidth() * scale,
                          getHeight() * scale);
            }
        } else {
            // Малюємо вогонь після приземлення
            float fireAlpha = Math.min(1.0f, fireTimer / FIRE_DURATION);
            Color oldColor = batch.getColor().cpy();
            batch.setColor(1, 1, 1, fireAlpha);
            batch.draw(fireTexture,
                      targetPosition.x - fireTexture.getWidth() / 2,
                      targetPosition.y - fireTexture.getHeight() / 2,
                      fireTexture.getWidth() * scale,
                      fireTexture.getHeight() * scale);
            batch.setColor(oldColor);
        }
    }

    @Override
    public void attack(Hero hero) {
        // Метеорит не атакує напряму
    }

    @Override
    public void dispose() {
        super.dispose();
        // Не потрібно викликати dispose() для warningTexture та fireTexture,
        // оскільки ці текстури керуються класом Assets
    }

    private Texture getTexture() {
        // Отримуємо текстуру з базового класу
        return super.texture;
    }
}
