package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.util.List;

public class Turret extends Enemy {
    private static final float ROTATION_SPEED = 90f; // Швидкість обертання в градусах за секунду
    private static final int BULLET_COUNT = 8; // Кількість куль за один постріл (для розширеного режиму)

    private Texture bulletTexture;
    private List<Bullet> bullets;
    private float currentAngle;
    private float targetAngle;
    private float rotationTimer;
    private float shootTimer;
    private TurretMode mode;
    private Texture baseTexture; // Текстура основи турелі

    public enum TurretMode {
        AIMED, // Стріляє в гравця
        ROTATING, // Обертається і стріляє
        RANDOM, // Стріляє в випадкових напрямках
        BURST // Стріляє чергами в різні боки
    }

    public Turret(float x, float y, GameMap gameMap, Rectangle roomBounds, List<Bullet> bullets, TurretMode mode) {
        // Турель має 5 HP, не рухається (швидкість 0) і завдає 1 шкоди
        super(Assets.turretTopTexture, x, y, 64, 64, 0f, 5, 1, new TurretAI(roomBounds), gameMap);

        this.bulletTexture = Assets.turretBulletTexture;
        this.bullets = bullets;
        this.currentAngle = 0;
        this.targetAngle = 0;
        this.rotationTimer = 0;
        this.shootTimer = 0;
        this.mode = mode;
        this.baseTexture = Assets.turretBaseTexture;

        // Встановлюємо значно меншу швидкість перезарядки для більшого спаму кулями
        switch (mode) {
            case AIMED:
                setAttackCooldown(0.17f); // Було 1.5f
                break;
            case ROTATING:
                setAttackCooldown(0.25f); // Було 0.8f
                break;
            case RANDOM:
                setAttackCooldown(0.35f); // Було 1.0f
                break;
            case BURST:
                setAttackCooldown(0.6f); // Було 2.5f
                break;
        }
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);

        // Оновлюємо кут повороту турелі
        updateRotation(hero, delta);

        // Оновлюємо таймер стрільби
        shootTimer -= delta;

        // Стріляємо, якщо можемо
        if (canAttack() && hasLineOfSight(hero)) {
            attack(hero);
        }
    }

    private void updateRotation(Hero hero, float delta) {
        switch (mode) {
            case AIMED:
                // Повертаємося до гравця
                targetAngle = calculateAngleToTarget(hero.getCenterX(), hero.getCenterY());
                break;

            case ROTATING:
                // Постійно обертаємося
                currentAngle += ROTATION_SPEED * delta;
                if (currentAngle >= 360) currentAngle -= 360;
                return; // Пропускаємо плавне обертання

            case RANDOM:
                // Змінюємо цільовий кут випадково
                rotationTimer -= delta;
                if (rotationTimer <= 0) {
                    targetAngle = MathUtils.random(0, 360);
                    rotationTimer = MathUtils.random(1.0f, 3.0f);
                }
                break;

            case BURST:
                // Не обертаємося, стріляємо в різні боки
                break;
        }

        // Плавно повертаємося до цільового кута
        float angleDiff = targetAngle - currentAngle;

        // Нормалізуємо різницю кутів до діапазону [-180, 180]
        while (angleDiff > 180) angleDiff -= 360;
        while (angleDiff < -180) angleDiff += 360;

        // Плавно повертаємося
        float rotationAmount = Math.min(ROTATION_SPEED * delta, Math.abs(angleDiff)) * Math.signum(angleDiff);
        currentAngle += rotationAmount;

        // Нормалізуємо кут
        while (currentAngle >= 360) currentAngle -= 360;
        while (currentAngle < 0) currentAngle += 360;
    }

    private float calculateAngleToTarget(float targetX, float targetY) {
        float dx = targetX - getCenterX();
        float dy = targetY - getCenterY();
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    private boolean hasLineOfSight(Hero hero) {
        Vector2 start = getCenter();
        Vector2 end = hero.getCenter();
        return getGameMap().hasLineOfSight(start, end);
    }

    @Override
    public void attack(Hero hero) {
        switch (mode) {
            case AIMED:
                // Стріляємо одну кулю прямо в гравця
                float angleToHero = calculateAngleToTarget(hero.getCenterX(), hero.getCenterY());
                shootBullet(angleToHero);
                break;

            case ROTATING:
                // Стріляємо дві кулі в поточному напрямку з невеликим розкидом
                shootBullet(currentAngle - 5);
                shootBullet(currentAngle + 5);
                break;

            case RANDOM:
                // Стріляємо три кулі в випадкових напрямках
                for (int i = 0; i < 3; i++) {
                    shootBullet(MathUtils.random(0, 360));
                }
                break;

            case BURST:
                // Стріляємо кілька куль у різних напрямках (360 градусів)
                float angleStep = 360f / BULLET_COUNT;
                for (int i = 0; i < BULLET_COUNT; i++) {
                    shootBullet(i * angleStep);
                }
                break;
        }

        resetAttackCooldown();
    }

    private void shootBullet(float angle) {
        bullets.add(new Bullet(getCenterX(), getCenterY(), angle, bulletTexture, true, this, 30, 10, 600));
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Малюємо основу турелі
        batch.draw(baseTexture, getX()+10, getY()-5, getWidth()-20, getHeight()-20);

        // Малюємо верхню частину турелі з поворотом
        batch.draw(
            getTexture(),
            getX(),
            getY(),
            getWidth() / 2, // Точка обертання X
            getHeight() / 2, // Точка обертання Y
            getWidth(),
            getHeight(),
            1, 1, // Масштаб
            currentAngle, // Кут повороту
            0, 0, // Початок текстури
            getTexture().getWidth(),
            getTexture().getHeight(),
            false, false // Віддзеркалення
        );
    }

    @Override
    public void dispose() {
        super.dispose();
        // Не потрібно викликати dispose() для bulletTexture та baseTexture,
        // оскільки ці текстури керуються класом Assets
    }

    private Texture getTexture() {
        // Отримуємо текстуру з базового класу
        return super.texture;
    }
}
