package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;

public class FightLevel {
    private final Rectangle roomArea;
    private final int maxEnemies;
    private boolean isActive;
    private final Random random;
    private final Texture bulletTexture;
    private final List<Bullet> bullets;
    private final GameMap gameMap;

    public FightLevel(float x, float y, float width, float height, int maxEnemies, Texture bulletTex, List<Bullet> bullets, GameMap map) {
        this.roomArea = new Rectangle(x, y, width, height);
        this.maxEnemies = maxEnemies;
        this.bulletTexture = bulletTex;
        this.bullets = bullets;
        this.gameMap = map;
        this.random = new Random();
    }

    public void activateIfNeeded(Hero hero, List<Enemy> enemies) {
        if (isActive || !roomArea.contains(hero.getBoundingRectangle())) return;

        isActive = true;
        spawnEnemies(enemies);
    }

    private void spawnEnemies(List<Enemy> enemies) {
        for (int i = 0; i < maxEnemies; i++) {
            Vector2 pos = findValidSpawnPosition();
            if (pos != null) {
                enemies.add(createEnemy(pos));
            }
        }
    }

    private Vector2 findValidSpawnPosition() {
        final int MAX_ATTEMPTS = 100;
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            Vector2 pos = new Vector2(
                    roomArea.x + random.nextFloat() * roomArea.width,
                    roomArea.y + random.nextFloat() * roomArea.height
            );

            if (isPositionValid(pos)) {
                return pos;
            }
        }
        return null;
    }

    private boolean isPositionValid(Vector2 pos) {
        Rectangle testArea = new Rectangle(pos.x - 25, pos.y - 25, 50, 50);
        return !gameMap.isCellBlocked(testArea);
    }

    private Enemy createEnemy(Vector2 pos) {
        return MathUtils.randomBoolean(0.5f) ?
                new ShortAttackEnemy(
                        new Texture("core/assets/short1.png"),
                        pos.x,
                        pos.y,
                        20,    // width
                        30,    // height
                        200f,  // speed
                        3,     // health
                        1,     // damage
                        1.5f,  // attackCooldown
                        gameMap, new ShortAttackAI(this.roomArea)
                ) :
                new LongAttackEnemy(
                        new Texture("core/assets/long1.png"),
                        pos.x,
                        pos.y,
                        20,
                        30,
                        180f,
                        3,
                        1,
                        1.0f,
                        bulletTexture,
                        bullets,
                        gameMap, new LongAttackAI(this.roomArea)
                );
    }
}
