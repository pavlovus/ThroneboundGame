package com.mygdx.darkknight.bosses;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.enemies.TurretAI;

import java.util.List;

public class JesterTurret extends Enemy {
    private static final float ROTATION_SPEED = 60f; // Градуси/секунду
    private static final float AIMED_SHOT_COOLDOWN = 0.5f; // Базова перезарядка для прицільних пострілів
    private static final float PATTERN_SHOT_COOLDOWN = 1.0f; // Базова перезарядка для патернів
    private static final float AIMED_SHOT_SPEED_PHASE_1 = 300f;
    private static final float AIMED_SHOT_SPEED_PHASE_2 = 400f;
    private static final float AIMED_SHOT_SPEED_PHASE_3 = 500f;
    private static final float PATTERN_SHOT_SPEED_PHASE_1 = 200f;
    private static final float PATTERN_SHOT_SPEED_PHASE_2 = 250f;
    private static final float PATTERN_SHOT_SPEED_PHASE_3 = 300f;
    private static final float CHAOS_SHOT_SPEED = 350f;

    private Jester boss;
    private float currentAngle;
    private float aimedShotTimer;
    private float patternShotTimer;
    private TurretMode mode;

    public enum TurretMode {
        PHASE_1, // Віялові залпи + прицільна стрільба
        PHASE_2, // Спіральні патерни + швидша прицільна
        PHASE_3 // Комбінація + хаотичний залп
    }

    public JesterTurret(float x, float y, GameMap gameMap, Rectangle roomBounds, List<Bullet> bullets, Jester boss) {
        super(Assets.jesterTurretTexture, x, y, 180, 180, 0f, 1, 1, bullets, new TurretAI(roomBounds), gameMap);
        this.boss = boss;
        this.currentAngle = 0;
        this.aimedShotTimer = AIMED_SHOT_COOLDOWN;
        this.patternShotTimer = PATTERN_SHOT_COOLDOWN;
        this.mode = TurretMode.PHASE_1;
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);

        // Якщо бос мертвий, турель також вмирає
        if (boss.isDead()) {
            setDead(true);
            return;
        }

        // Оновлюємо обертання
        currentAngle += ROTATION_SPEED * delta;
        if (currentAngle >= 360) currentAngle -= 360;

        // Оновлюємо таймери
        aimedShotTimer -= delta;
        patternShotTimer -= delta;

        // Прицільна стрільба
        if (aimedShotTimer <= 0 && hasLineOfSight(hero)) {
            shootAimed(hero);
            aimedShotTimer = getAimedShotCooldown();
        }

        // Патерни "bullet hell"
        if (patternShotTimer <= 0) {
            shootPattern();
            patternShotTimer = getPatternShotCooldown();
        }
    }

    @Override
    public void takeDamage(int damage) {
        // Турель прозора для урону - весь урон йде до боса
        if (!boss.isDead()) {
            boss.takeDamage(damage);
            return;
        }
        // Якщо бос вже мертвий, турель теж не отримує урону (вона вже мертва)
    }

    private float getAimedShotCooldown() {
        switch (mode) {
            case PHASE_1: return AIMED_SHOT_COOLDOWN; // 0.5 с
            case PHASE_2: return AIMED_SHOT_COOLDOWN * 0.8f; // 0.4 с
            case PHASE_3: return AIMED_SHOT_COOLDOWN * 0.6f; // 0.3 с
        }
        return AIMED_SHOT_COOLDOWN;
    }

    private float getPatternShotCooldown() {
        switch (mode) {
            case PHASE_1: return PATTERN_SHOT_COOLDOWN; // 1.0 с
            case PHASE_2: return PATTERN_SHOT_COOLDOWN * 0.8f; // 0.8 с
            case PHASE_3: return PATTERN_SHOT_COOLDOWN * 0.6f; // 0.6 с
        }
        return PATTERN_SHOT_COOLDOWN;
    }

    private void shootAimed(Hero hero) {
        float angle = calculateAngleToTarget(hero.getCenterX(), hero.getCenterY());
        float speed = AIMED_SHOT_SPEED_PHASE_1; // default

        switch (mode) {
            case PHASE_2:
                speed = AIMED_SHOT_SPEED_PHASE_2;
                break;
            case PHASE_3:
                speed = AIMED_SHOT_SPEED_PHASE_3;
                break;
        }

        shootBullet(angle, speed, 10);
    }

    private void shootPattern() {
        switch (mode) {
            case PHASE_1:
                for (int i = -2; i <= 2; i++) {
                    shootBullet(currentAngle + i * 30, PATTERN_SHOT_SPEED_PHASE_1, 1);
                }
                break;
            case PHASE_2:
                for (int i = 0; i < 8; i++) {
                    shootBullet(currentAngle + i * 45 + patternShotTimer * 180, PATTERN_SHOT_SPEED_PHASE_2, 1);
                }
                break;
            case PHASE_3:
                for (int i = -2; i <= 2; i++) {
                    shootBullet(currentAngle + i * 30, PATTERN_SHOT_SPEED_PHASE_3, 1);
                }
                for (int i = 0; i < 6; i++) {
                    shootBullet(currentAngle + i * 60 + patternShotTimer * 360, PATTERN_SHOT_SPEED_PHASE_3, 1);
                }
                if (MathUtils.randomBoolean(0.2f)) {
                    for (int i = 0; i < 12; i++) {
                        shootBullet(i * 30, CHAOS_SHOT_SPEED, 1);
                    }
                }
                break;
        }
    }

    private void shootBullet(float angle, float speed, int damage) {
        String animationPath = "core/assets/sparkle.png";
        bullets.add(new Bullet(getCenterX(), getCenterY(), angle, Assets.turretBulletTexture, animationPath, true, this, 20, 20, speed));
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
        // Атака обробляється в update
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Малюємо турель з обертанням з врахуванням TextureRegion
        batch.draw(
            texture,
            getX(),
            getY(),
            getWidth() / 2,
            getHeight() / 2,
            getWidth(),
            getHeight(),
            1, 1,
            currentAngle,
            0, 0,
            texture.getWidth(),
            texture.getHeight(),
            false, false
        );
    }

    public void setMode(TurretMode mode) {
        this.mode = mode;
    }
}
