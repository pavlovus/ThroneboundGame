package com.mygdx.darkknight.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.*;

import java.util.List;

public class Queen extends Enemy {
    private static final int MAX_HEALTH = 30; // Повернуто до 2000 для балансу
    private static final float PHASE_2_THRESHOLD = 0.66f; // 66% HP
    private static final float PHASE_3_THRESHOLD = 0.33f; // 33% HP
    private static final float BULLET_COOLDOWN = 1.5f;
    private static final float MINION_SPAWN_COOLDOWN = 15.0f;
    private static final float ROTATION_SPEED = 60f; // Градуси/секунду для патернів
    private static final int BULLET_WIDTH = 20;
    private static final int BULLET_HEIGHT = 20;

    private float bulletTimer;
    private float minionTimer;
    private float currentAngle;
    private Rectangle roomBounds;
    private Phase currentPhase;
    private List<Enemy> currentWaveEnemies;
    private List<Enemy> enemiesToAdd;

    private enum Phase {
        PHASE_1, PHASE_2, PHASE_3
    }

    public Queen(float x, float y, GameMap gameMap, Rectangle roomBounds, List<Bullet> bullets, List<Enemy> currentWaveEnemies, List<Enemy> enemiesToAdd) {
        super(Assets.queenTexture, x, y, 96, 96, 0f, MAX_HEALTH, 1, bullets, new QueenAI(roomBounds), gameMap, true);
        this.roomBounds = roomBounds;
        this.bulletTimer = BULLET_COOLDOWN;
        this.minionTimer = MINION_SPAWN_COOLDOWN;
        this.currentAngle = 0;
        this.currentPhase = Phase.PHASE_1;
        this.currentWaveEnemies = currentWaveEnemies;
        this.enemiesToAdd = enemiesToAdd;
    }

    @Override
    public void attack(Hero hero) {
        // Атака обробляється в update
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);
        if (isDead()) return;

        // Оновлюємо кут для патернів
        currentAngle += ROTATION_SPEED * delta;
        if (currentAngle >= 360) currentAngle -= 360;

        // Оновлюємо фазу
        float healthRatio = (float) getHealth() / MAX_HEALTH;
        if (healthRatio <= PHASE_3_THRESHOLD) {
            currentPhase = Phase.PHASE_3;
        } else if (healthRatio <= PHASE_2_THRESHOLD) {
            currentPhase = Phase.PHASE_2;
        } else {
            currentPhase = Phase.PHASE_1;
        }

        // Оновлюємо таймери
        bulletTimer -= delta;
        minionTimer -= delta;

        // Стріляємо кулями
        if (bulletTimer <= 0) {
            shootPattern(hero);
            bulletTimer = getBulletCooldown();
        }

        // Спавнимо міньйонів
        if (minionTimer <= 0) {
            spawnMinions();
            minionTimer = getMinionCooldown();
        }
    }

    private float getBulletCooldown() {
        switch (currentPhase) {
            case PHASE_1:
                return BULLET_COOLDOWN; // 1.0 с
            case PHASE_2:
                return BULLET_COOLDOWN * 0.8f; // 0.8 с
            case PHASE_3:
                return BULLET_COOLDOWN * 0.6f; // 0.6 с
        }
        return BULLET_COOLDOWN;
    }

    private float getMinionCooldown() {
        switch (currentPhase) {
            case PHASE_1:
                return MINION_SPAWN_COOLDOWN; // 10.0 с
            case PHASE_2:
                return MINION_SPAWN_COOLDOWN * 0.8f; // 8.0 с
            case PHASE_3:
                return MINION_SPAWN_COOLDOWN * 0.6f; // 6.0 с
        }
        return MINION_SPAWN_COOLDOWN;
    }

    private void shootPattern(Hero hero) {
        float angleToHero = calculateAngleToTarget(hero.getCenterX(), hero.getCenterY());
        switch (currentPhase) {
            case PHASE_1:
                // Віяловий залп: 5 куль, 20° між ними, центрований на героя
                for (int i = -2; i <= 2; i++) {
                    shootBullet(getCenterX(), getCenterY(), angleToHero + i * 20, 80f);
                }
                break;
            case PHASE_2:
                // Спіральний патерн: 6 куль, обертаються навколо кута до героя
                for (int i = 0; i < 6; i++) {
                    shootBullet(getCenterX(), getCenterY(), angleToHero + currentAngle + i * 60, 100f);
                }
                break;
            case PHASE_3:
                // Комбінація: віяло + спіраль + кільце
                for (int i = -3; i <= 3; i++) {
                    shootBullet(getCenterX(), getCenterY(), angleToHero + i * 15, 80f);
                }
                for (int i = 0; i < 8; i++) {
                    shootBullet(getCenterX(), getCenterY(), angleToHero + currentAngle + i * 45, 150f);
                }
                if (MathUtils.randomBoolean(0.3f)) { // 30% шанс на кільце
                    for (int i = 0; i < 12; i++) {
                        shootBullet(getCenterX(), getCenterY(), angleToHero + i * 30, 120f);
                    }
                }
                break;
        }
    }

    private void shootBullet(float x, float y, float angle, float speed) {
        bullets.add(new Bullet(x, y, angle, Assets.queenBulletTexture,true, this, BULLET_WIDTH, BULLET_HEIGHT, speed));
    }

    private void spawnMinions() {
        int minionCount = currentPhase == Phase.PHASE_1 ? 2 : currentPhase == Phase.PHASE_2 ? 3 : 4;
        for (int i = 0; i < minionCount; i++) {
            float spawnX = roomBounds.x + MathUtils.random(roomBounds.width - 32);
            float spawnY = roomBounds.y + MathUtils.random(roomBounds.height - 32);
            LongAttackEnemy minion = new LongAttackEnemy(
                Assets.long_3Texture, spawnX, spawnY, 32, 32, 70f, 3, 1, 1f, Assets.queenBulletTexture, bullets, gameMap, new LongAttackAI(roomBounds)
            );
            enemiesToAdd.add(minion);
        }
    }

    private float calculateAngleToTarget(float targetX, float targetY) {
        float dx = targetX - getCenterX();
        float dy = targetY - getCenterY();
        return (float) Math.toDegrees(Math.atan2(dy, dx));
    }
}
