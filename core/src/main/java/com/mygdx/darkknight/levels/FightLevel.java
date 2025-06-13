package com.mygdx.darkknight.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.*;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.weapons.*;

import java.util.*;

public abstract class FightLevel {
    protected final Rectangle roomArea;
    protected int maxEnemiesPerWave; // Змінено на кількість ворогів на хвилю
    protected Texture bulletTexture;
    protected List<Bullet> bullets;
    protected GameMap gameMap;
    protected Hero hero;
    protected SpriteBatch batch;

    protected int totalWaves;
    protected int currentWave = 0;
    protected float waveDelayTimer = 0f;
    protected final float delayBetweenWaves = 1.5f;
    protected final float delayBeforeDoorOpen = 1f; // Затримка перед відкриттям дверей

    protected List<Enemy> currentWaveEnemies; // Список ворогів поточної хвилі

    protected enum LevelState {
        INACTIVE, ACTIVE, WAITING_NEXT_WAVE, WAITING_FOR_DOOR_OPEN, COMPLETED
    }
    protected LevelState state = LevelState.INACTIVE;

    protected final Random random = new Random();

    public FightLevel(Hero hero, SpriteBatch batch, GameMap gameMap, float x, float y, float width, float height) {
        this.batch = batch;
        this.hero = hero;
        this.gameMap = gameMap;
        this.roomArea = new Rectangle(x, y, width, height);
        this.currentWaveEnemies = new ArrayList<>(); // Ініціалізуємо список ворогів поточної хвилі
    }

    public void update(float deltaTime, Hero hero, List<Enemy> globalEnemies) {
        Iterator<Enemy> iterator = currentWaveEnemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.isDead()) {
                iterator.remove();
            }
        }

        switch (state) {
            case INACTIVE:
                if (roomArea.contains(hero.getBoundingRectangle()) && !gameMap.isTouchingDoors(hero.getBoundingRectangle())) {
                    state = LevelState.ACTIVE;
                    currentWave = 1; // Починаємо з першої хвилі
                    spawnEnemies(globalEnemies);
                    gameMap.closeDoors();
                }
                break;

            case ACTIVE:
                if (currentWaveEnemies.isEmpty()) { // Перевіряємо, чи всі вороги поточної хвилі мертві
                    if (currentWave >= totalWaves) {
                        state = LevelState.WAITING_FOR_DOOR_OPEN;
                        waveDelayTimer = delayBeforeDoorOpen; // Затримка перед відкриттям дверей
                    } else {
                        state = LevelState.WAITING_NEXT_WAVE;
                        waveDelayTimer = delayBetweenWaves;
                    }
                }
                break;

            case WAITING_FOR_DOOR_OPEN:
                waveDelayTimer -= deltaTime;
                if (waveDelayTimer <= 0f) {
                    state = LevelState.COMPLETED;
                    gameMap.openDoors();
                }
                break;

            case WAITING_NEXT_WAVE:
                waveDelayTimer -= deltaTime;
                if (waveDelayTimer <= 0f) {
                    currentWave++;
                    state = LevelState.ACTIVE;
                    spawnEnemies(globalEnemies);
                }
                break;

            case COMPLETED:
                break;
        }
    }

    protected void spawnEnemies(List<Enemy> globalEnemies) {
        currentWaveEnemies.clear(); // Очищаємо список ворогів поточної хвилі перед спавном
        for (int i = 0; i < maxEnemiesPerWave; i++) { // Використовуємо maxEnemiesPerWave
            Vector2 pos = findValidSpawnPosition();
            if (pos != null) {
                Enemy newEnemy = createEnemy(pos);
                globalEnemies.add(newEnemy);
                currentWaveEnemies.add(newEnemy); // Додаємо ворога до списку поточної хвилі
            }
        }
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

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public String getStateName() {
        return state.name();
    }
}
