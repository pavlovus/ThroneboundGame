package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.util.List;
import java.util.Random;

public class Matriarch extends Enemy {
    private static final float SPAWN_COOLDOWN = 4.0f;
    private static final int MAX_MINIONS_PER_MATRIARCH = 5;
    private static final int MAX_SPAWN_ATTEMPTS = 10;
    private static final float SPAWN_RADIUS = 20f;

    private float spawnTimer;
    private List<Enemy> enemies;
    private List<Enemy> enemiesToAdd;
    private Rectangle roomBounds;
    private GameMap gameMap;
    private Random random;
    private Texture minionTexture;
    protected boolean flip = false; // Нове поле для фліпу текстури

    public Matriarch(Texture matriarchTexture, Texture minionTexture, float x, float y, GameMap gameMap, Rectangle roomBounds, List<Enemy> enemies, List<Enemy> enemiesToAdd, List<Bullet> bullets) {
        super(matriarchTexture, x, y, 40, 40, 80f, 5, 0, bullets, new MatriarchAI(roomBounds, new Vector2(x,y)), gameMap, false);
        this.setAttackCooldown(SPAWN_COOLDOWN);
        this.spawnTimer = SPAWN_COOLDOWN;
        this.enemies = enemies;
        this.enemiesToAdd = enemiesToAdd;
        this.roomBounds = roomBounds;
        this.gameMap = gameMap;
        this.random = new Random();
        this.minionTexture = minionTexture;
    }

    @Override
    public void attack(Hero hero) {
        // Матріарх не атакує героя безпосередньо, вона спавнить міньйонів.
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);
        // Встановлюємо фліп на основі позиції героя
        setFlip(getCenterX() < hero.getCenterX()); // Фліп, якщо герой праворуч
        spawnTimer -= delta;
        if (spawnTimer <= 0) {
            spawnMinion();
            spawnTimer = SPAWN_COOLDOWN;
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Використовуємо змінну flip для дзеркального відображення текст ury
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

    private void spawnMinion() {
        if (getAliveMinionsCount() >= MAX_MINIONS_PER_MATRIARCH) {
            return;
        }

        Vector2 spawnPos = findValidSpawnPosition();
        if (spawnPos == null) {
            return;
        }

        ShortAttackEnemy minion = new ShortAttackEnemy(
            this.minionTexture, spawnPos.x, spawnPos.y,
            32, 32, 60f, 3, 1, 0.8f, bullets, gameMap, new ShortAttackAI(roomBounds)
        );
        enemies.add(minion);
        enemiesToAdd.add(minion);
    }

    private Vector2 findValidSpawnPosition() {
        for (int i = 0; i < MAX_SPAWN_ATTEMPTS; i++) {
            float spawnX = getCenterX() + (random.nextFloat() * 2 - 1) * SPAWN_RADIUS;
            float spawnY = getCenterY() + (random.nextFloat() * 2 - 1) * SPAWN_RADIUS;

            spawnX = Math.max(roomBounds.x, Math.min(spawnX, roomBounds.x + roomBounds.width - minionTexture.getWidth()));
            spawnY = Math.max(roomBounds.y, Math.min(spawnY, roomBounds.y + roomBounds.height - minionTexture.getHeight()));

            Rectangle testArea = new Rectangle(spawnX, spawnY, 32, 32);
            if (!gameMap.isCellBlocked(testArea)) {
                return new Vector2(spawnX, spawnY);
            }
        }
        return null;
    }

    private int getAliveMinionsCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            if (enemy instanceof ShortAttackEnemy && !enemy.isDead()) {
                count++;
            }
        }
        return count;
    }

    // Нові методи для керування фліпом
    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean isFlip() {
        return flip;
    }
}
