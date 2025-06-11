package com.mygdx.darkknight.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.List;
import java.util.Random;

public abstract class FightLevel {
    protected final Rectangle roomArea;
    protected int maxEnemies;
    protected Texture bulletTexture;
    protected List<Bullet> bullets;
    protected GameMap gameMap;

    protected int totalWaves;
    protected int currentWave = 0;
    protected float waveDelayTimer = 0f;
    protected final float delayBetweenWaves = 3f;

    protected enum LevelState {
        INACTIVE, ACTIVE, WAITING_NEXT_WAVE, COMPLETED
    }
    protected LevelState state = LevelState.INACTIVE;

    protected final Random random = new Random();

    public FightLevel(float x, float y, float width, float height) {
        this.roomArea = new Rectangle(x, y, width, height);
    }

    public void update(float deltaTime, Hero hero, List<Enemy> globalEnemies) {
        switch (state) {
            case INACTIVE:
                if (roomArea.contains(hero.getBoundingRectangle()) && !gameMap.isTouchingDoors(hero.getBoundingRectangle())) {
                    state = LevelState.ACTIVE;
                    spawnEnemies(globalEnemies);
                    gameMap.closeDoors();
                }
                break;

            case ACTIVE:
                if (!checkIfEnemiesAreAliveInRoom(globalEnemies)) {
                    currentWave++;
                    if (currentWave >= totalWaves) {
                        state = LevelState.COMPLETED;
                        gameMap.openDoors();
                    } else {
                        state = LevelState.WAITING_NEXT_WAVE;
                        waveDelayTimer = delayBetweenWaves;
                    }
                }
                break;

            case WAITING_NEXT_WAVE:
                waveDelayTimer -= deltaTime;
                if (waveDelayTimer <= 0f) {
                    state = LevelState.ACTIVE;
                    spawnEnemies(globalEnemies);
                }
                break;

            case COMPLETED:
                break;
        }
    }

    protected void spawnEnemies(List<Enemy> enemies) {
        for (int i = 0; i < maxEnemies; i++) {
            Vector2 pos = findValidSpawnPosition();
            if (pos != null) {
                enemies.add(createEnemy(pos));
            }
        }
    }

    protected boolean checkIfEnemiesAreAliveInRoom(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (!enemy.isDead() && roomArea.contains(enemy.getBoundingRectangle())) {
                return true;
            }
        }
        return false;
    }

    protected Vector2 findValidSpawnPosition() {
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

    protected boolean isPositionValid(Vector2 pos) {
        Rectangle testArea = new Rectangle(pos.x - 25, pos.y - 25, 50, 50);
        return !gameMap.isCellBlocked(testArea);
    }

    protected abstract Enemy createEnemy(Vector2 pos);

    public String getStateName() {
        return state.name();
    }
}
