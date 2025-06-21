package com.mygdx.darkknight.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.bosses.ButcherAI;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.enemies.ShortAttackAI;
import com.mygdx.darkknight.enemies.ShortAttackEnemy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Butcher extends Enemy {
    private static final float CLEAVER_THROW_COOLDOWN = 2f;
    private static final float CHARGE_ATTACK_COOLDOWN = 20.0f;
    private static final float CHARGE_PREP_TIME = 1.5f;
    private static final float POST_CHARGE_DELAY = 2.0f; // Затримка після ривка
    private static final float MINION_SPAWN_COOLDOWN = 12.0f;
    private static final float NORMAL_SPEED = 20f;
    private static final float CHARGE_SPEED = 150f;
    private static final int MINIONS_PER_SPAWN = 2;
    private static final int CHARGE_DAMAGE = 30;

    private float cleaverThrowTimer = CLEAVER_THROW_COOLDOWN;
    private float chargeAttackTimer = CHARGE_ATTACK_COOLDOWN;
    private float chargePrepTimer = 0f;
    private float minionSpawnTimer = MINION_SPAWN_COOLDOWN;
    private float postChargeDelayTimer = 0f; // Таймер затримки після ривка

    private boolean isPreparingCharge = false;
    private boolean isCharging = false;
    private boolean attackedByCharge = false;
    private Vector2 chargeTarget;
    private Vector2 chargeDirection;

    private List<Enemy> currentWaveEnemies;
    private List<Enemy> globalEnemies;
    private List<Enemy> enemiesToAdd;
    private List<Bullet> bullets;
    private Rectangle roomBounds;
    private Random random = new Random();

    private List<Bullet> pendingBullets = new ArrayList<>();

    protected boolean flip = false; // Новое поле для флипа текстуры

    public Butcher(float x, float y, GameMap gameMap, Rectangle roomBounds, List<Bullet> bullets, List<Enemy> currentWaveEnemies, List<Enemy> enemiesToAdd) {
        super(Assets.butcherTexture, x, y, 110, 110, NORMAL_SPEED, 100, 1, bullets, new ButcherAI(roomBounds), gameMap, true);
        this.roomBounds = roomBounds;
        this.bullets = bullets;
        this.currentWaveEnemies = currentWaveEnemies;
        this.enemiesToAdd = enemiesToAdd;
        this.globalEnemies = enemiesToAdd;
    }

    @Override
    public void attack(Hero hero) {
        // Не используется
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);
        if (isDead()) return;

        // Устанавливаем флип на основе позиции героя, если не в ривке
        if (!isPreparingCharge && !isCharging) {
            setFlip(getCenterX() < hero.getCenterX()); // Флип, если герой справа
        }

        cleaverThrowTimer -= delta;
        chargeAttackTimer -= delta;
        minionSpawnTimer -= delta;
        postChargeDelayTimer -= delta;

        if (isPreparingCharge) {
            chargePrepTimer -= delta;
            if (chargePrepTimer <= 0) {
                isPreparingCharge = false;
                isCharging = true;
                attackedByCharge = false;
                setSpeed(CHARGE_SPEED);
                chargeTarget = hero.getCenter().cpy();
                chargeDirection = chargeTarget.cpy().sub(getCenter()).nor();
            }
        } else if (isCharging) {
            if (getBoundingRectangle().overlaps(hero.getBoundingRectangle()) && !attackedByCharge) {
                hero.takeDamage(CHARGE_DAMAGE, armorIgnore);
                attackedByCharge = true;
            }
            if (getX() <= roomBounds.x + 20 || getX() + getWidth() >= roomBounds.x + roomBounds.width - 20 ||
                getY() <= roomBounds.y + 20 || getY() + getHeight() >= roomBounds.y + roomBounds.height - 20) {
                isCharging = false;
                setSpeed(NORMAL_SPEED);
                chargeAttackTimer = CHARGE_ATTACK_COOLDOWN;
                attackedByCharge = false;
                postChargeDelayTimer = POST_CHARGE_DELAY;
            }
        } else {
            if (chargeAttackTimer <= 0) {
                isPreparingCharge = true;
                chargePrepTimer = CHARGE_PREP_TIME;
                chargeAttackTimer = CHARGE_ATTACK_COOLDOWN;
            }
        }

        if (cleaverThrowTimer <= 0 && !isPreparingCharge && !isCharging && postChargeDelayTimer <= 0) {
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

    @Override
    public void draw(SpriteBatch batch) {
        // Используем переменную flip для зеркального отображения текстуры
        batch.draw(texture, x, y, getWidth()/2f, getHeight()/2f, (float) getWidth(), (float) getHeight(), 1, 1, 0f, 0, 0, texture.getWidth(), texture.getHeight(), flip, false);
        // Рисуем индикаторы урона
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

    private void throwCleaver(Hero hero) {
        float angle = (float) Math.toDegrees(Math.atan2(hero.getCenterY() - getCenterY(), hero.getCenterX() - getCenterX()));
        Bullet cleaver = new Bullet(getCenterX(), getCenterY(), angle, Assets.cleaverTexture, true, this, 20, 40, 200f);
        pendingBullets.add(cleaver);
    }

    private void spawnMinions() {
        for (int i = 0; i < MINIONS_PER_SPAWN; i++) {
            float spawnX = getCenterX() + (random.nextFloat() * 2 - 1) * 50;
            float spawnY = getCenterY() + (random.nextFloat() * 2 - 1) * 50;
            spawnX = MathUtils.clamp(spawnX, roomBounds.x, roomBounds.x + roomBounds.width - 32);
            spawnY = MathUtils.clamp(spawnY, roomBounds.y, roomBounds.y + roomBounds.height - 32);

            ShortAttackEnemy minion = new ShortAttackEnemy(
                Assets.short_1Texture, spawnX, spawnY, 32, 32, 200f, 3, 1, 1, bullets, gameMap, new ShortAttackAI(roomBounds)
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

    // Новые методы для управления флипом
    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean isFlip() {
        return flip;
    }
}
