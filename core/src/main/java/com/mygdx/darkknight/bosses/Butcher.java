package com.mygdx.darkknight.bosses;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.enemies.ShortAttackAI;
import com.mygdx.darkknight.enemies.ShortAttackEnemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Butcher extends Enemy {
    private static final float CLEAVER_THROW_COOLDOWN = 1.5f;
    private static final float CHARGE_ATTACK_COOLDOWN = 10.0f;
    private static final float CHARGE_PREP_TIME = 1.5f;
    private static final float MINION_SPAWN_COOLDOWN = 6.0f;
    private static final float NORMAL_SPEED = 100f;
    private static final float CHARGE_SPEED = 700f;
    private static final int MINIONS_PER_SPAWN = 2;
    private static final int CHARGE_DAMAGE = 30;

    private float cleaverThrowTimer = CLEAVER_THROW_COOLDOWN;
    private float chargeAttackTimer = CHARGE_ATTACK_COOLDOWN;
    private float chargePrepTimer = 0f;
    private float minionSpawnTimer = MINION_SPAWN_COOLDOWN;

    private boolean isPreparingCharge = false;
    private boolean isCharging = false;
    private Vector2 chargeTarget;
    private Vector2 chargeDirection;

    private List<Enemy> currentWaveEnemies;
    private List<Enemy> globalEnemies;
    private List<Enemy> enemiesToAdd;
    private List<Bullet> bullets;
    private Rectangle roomBounds;
    private Random random = new Random();

    private List<Bullet> pendingBullets = new ArrayList<>();

    public Butcher(float x, float y, GameMap gameMap, Rectangle roomBounds, List<Bullet> bullets, List<Enemy> currentWaveEnemies, List<Enemy> enemiesToAdd) {
        super(Assets.butcherTexture, x, y, 110, 110, NORMAL_SPEED, 30, 10, bullets, new ButcherAI(roomBounds), gameMap);
        this.roomBounds = roomBounds;
        this.bullets = bullets;
        this.currentWaveEnemies = currentWaveEnemies;
        this.enemiesToAdd = enemiesToAdd;
        this.globalEnemies = enemiesToAdd;
    }

    @Override
    public void attack(Hero hero) {
        // Не використовується
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);
        if (isDead()) return;

        cleaverThrowTimer -= delta;
        chargeAttackTimer -= delta;
        minionSpawnTimer -= delta;

        if (isPreparingCharge) {
            chargePrepTimer -= delta;
            if (chargePrepTimer <= 0) {
                isPreparingCharge = false;
                isCharging = true;
                setSpeed(CHARGE_SPEED);
                chargeTarget = hero.getCenter().cpy(); // Фіксуємо позицію героя на початку ривка
                chargeDirection = chargeTarget.cpy().sub(getCenter()).nor();
            }
        } else if (isCharging) {
            if (getBoundingRectangle().overlaps(hero.getBoundingRectangle())) {
                hero.takeDamage(CHARGE_DAMAGE);
            }
            // Зупиняємо ривок при зіткненні зі стіною
            if (getX() <= roomBounds.x || getX() + getWidth() >= roomBounds.x + roomBounds.width ||
                getY() <= roomBounds.y || getY() + getHeight() >= roomBounds.y + roomBounds.height) {
                isCharging = false;
                setSpeed(NORMAL_SPEED);
                chargeAttackTimer = CHARGE_ATTACK_COOLDOWN;
            }
        } else {
            if (chargeAttackTimer <= 0) {
                isPreparingCharge = true;
                chargePrepTimer = CHARGE_PREP_TIME;
                chargeAttackTimer = CHARGE_ATTACK_COOLDOWN;
            }
        }

        if (cleaverThrowTimer <= 0) {
            throwCleaver(hero);
            cleaverThrowTimer = CLEAVER_THROW_COOLDOWN;
        }
        if (minionSpawnTimer <= 0) {
            spawnMinions();
            minionSpawnTimer = MINION_SPAWN_COOLDOWN;
        }

        bullets.addAll(pendingBullets);
        pendingBullets.clear();
    }

    private void throwCleaver(Hero hero) {
        float angle = (float) Math.toDegrees(Math.atan2(hero.getCenterY() - getCenterY(), hero.getCenterX() - getCenterX()));
        Bullet cleaver = new Bullet(getCenterX(), getCenterY(), angle, Assets.cleaverTexture, "core/assets/sparkle.png", true, this, 20, 40, 200f);
        pendingBullets.add(cleaver);
    }

    private void spawnMinions() {
        for (int i = 0; i < MINIONS_PER_SPAWN; i++) {
            float spawnX = getCenterX() + (random.nextFloat() * 2 - 1) * 50;
            float spawnY = getCenterY() + (random.nextFloat() * 2 - 1) * 50;
            spawnX = MathUtils.clamp(spawnX, roomBounds.x, roomBounds.x + roomBounds.width - 32);
            spawnY = MathUtils.clamp(spawnY, roomBounds.y, roomBounds.y + roomBounds.height - 32);

            ShortAttackEnemy minion = new ShortAttackEnemy(
                Assets.short_1Texture, spawnX, spawnY, 32, 32, 200f, 15, 10, 0.7f, bullets, gameMap, new ShortAttackAI(roomBounds)
            );
            currentWaveEnemies.add(minion);
            enemiesToAdd.add(minion);
        }
    }

    public boolean isPreparingCharge() {
        return isPreparingCharge;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public Vector2 getChargeDirection() {
        return chargeDirection;
    }
}
