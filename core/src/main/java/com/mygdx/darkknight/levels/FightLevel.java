package com.mygdx.darkknight.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.enemies.MeteorStrike;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class FightLevel {
    protected final Rectangle roomArea;
    protected int maxEnemiesPerWave; // Змінено з maxEnemies
    protected Texture bulletTexture;
    protected List<Bullet> bullets;
    protected GameMap gameMap;

    protected int totalWaves;
    protected int currentWave = 0;
    protected float waveDelayTimer = 0f;
    protected final float delayBetweenWaves = 1.5f; // Можна змінити назад на 3f, якщо потрібно
    protected final float delayBeforeDoorOpen = 1f;

    protected List<Enemy> currentWaveEnemies;

    protected List<MeteorStrike> activeMeteorStrikes;
    protected float meteorStrikeSpawnCooldown;
    private float meteorStrikeTimer = 0f;
    protected Texture meteorWarningTexture;
    protected Texture meteorExplosionTexture;
    protected int meteorStrikeDamage;

    public void updateMeteorStrikes(float deltaTime, Hero hero) {
        // Оновлюємо таймер спавну метеоритів
        meteorStrikeTimer -= deltaTime;
        if (meteorStrikeTimer <= 0 && state == LevelState.ACTIVE) {
            spawnMeteorStrike(hero); // Викликаємо спавн, якщо час настав і рівень активний
            meteorStrikeTimer = meteorStrikeSpawnCooldown; // Скидаємо таймер
        }

        // Оновлюємо та видаляємо завершені метеоритні удари
        Iterator<MeteorStrike> iterator = activeMeteorStrikes.iterator();
        while (iterator.hasNext()) {
            MeteorStrike strike = iterator.next();
            strike.update(deltaTime); // Оновлюємо стан метеоритного удару
            if (strike.isFinished()) {
                iterator.remove(); // Видаляємо завершений удар
            }
        }
    }

    public enum LevelState {
        INACTIVE, ACTIVE, WAITING_NEXT_WAVE, WAITING_FOR_DOOR_OPEN, COMPLETED
    }
    protected LevelState state = LevelState.INACTIVE;

    protected final Random random = new Random();

    // КОНСТРУКТОР 1: Звичайний конструктор (без метеоритів за замовчуванням)
    public FightLevel(float x, float y, float width, float height) {
        this.roomArea = new Rectangle(x, y, width, height);
        this.currentWaveEnemies = new ArrayList<>();
        this.activeMeteorStrikes = new ArrayList<>();
        this.meteorStrikeSpawnCooldown = Float.MAX_VALUE; // Дуже велике значення, щоб метеорити не спавнились
        this.meteorStrikeDamage = 0; // Немає шкоди
        this.meteorWarningTexture = null;
        this.meteorExplosionTexture = null;
    }

    // КОНСТРУКТОР 2: Конструктор з підтримкою метеоритів
    public FightLevel(float x, float y, float width, float height, float meteorStrikeSpawnCooldown, int meteorStrikeDamage, Texture meteorWarningTexture, Texture meteorExplosionTexture) {
        this.roomArea = new Rectangle(x, y, width, height);
        this.currentWaveEnemies = new ArrayList<>();
        this.activeMeteorStrikes = new ArrayList<>();
        this.meteorStrikeSpawnCooldown = meteorStrikeSpawnCooldown;
        this.meteorStrikeDamage = meteorStrikeDamage;
        this.meteorWarningTexture = meteorWarningTexture;
        this.meteorExplosionTexture = meteorExplosionTexture;
    }

    public void update(float deltaTime, Hero hero, List<Enemy> globalEnemies) {
        Iterator<Enemy> iterator = currentWaveEnemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            if (enemy.isDead()) {
                iterator.remove();
            }
        }

        // Оновлюємо та видаляємо метеорити, лише якщо текстури існують
        if (meteorWarningTexture != null && meteorExplosionTexture != null) {
            Iterator<MeteorStrike> meteorStrikeIterator = activeMeteorStrikes.iterator();
            while (meteorStrikeIterator.hasNext()) {
                MeteorStrike strike = meteorStrikeIterator.next();
                strike.update(deltaTime);
                if (strike.isFinished()) {
                    meteorStrikeIterator.remove();
                }
            }
        }


        switch (state) {
            case INACTIVE:
                // Додана перевірка !gameMap.isDoorsClosed()
                if (roomArea.contains(hero.getBoundingRectangle()) && !gameMap.isDoorsClosed()) {
                    state = LevelState.ACTIVE;
                    currentWave = 1;
                    spawnEnemies(globalEnemies);
                    gameMap.closeDoors();
                    // Запускаємо таймер метеоритів лише якщо вони підтримуються
                    if (meteorWarningTexture != null && meteorExplosionTexture != null) {
                        meteorStrikeTimer = meteorStrikeSpawnCooldown;
                    }
                }
                break;

            case ACTIVE:
                // Оновлюємо таймер і спавнимо метеорити лише якщо вони підтримуються
                if (meteorWarningTexture != null && meteorExplosionTexture != null) {
                    meteorStrikeTimer -= deltaTime;
                    if (meteorStrikeTimer <= 0f) {
                        spawnMeteorStrike(hero);
                        meteorStrikeTimer = meteorStrikeSpawnCooldown;
                    }
                }


                if (currentWaveEnemies.isEmpty()) {
                    if (currentWave >= totalWaves) {
                        state = LevelState.WAITING_FOR_DOOR_OPEN;
                        waveDelayTimer = delayBeforeDoorOpen;
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
        currentWaveEnemies.clear();
        for (int i = 0; i < maxEnemiesPerWave; i++) { // Змінено з maxEnemies
            Vector2 pos = findValidSpawnPosition();
            if (pos != null) {
                Enemy newEnemy = createEnemy(pos);
                globalEnemies.add(newEnemy);
                currentWaveEnemies.add(newEnemy);
            }
        }
    }

    protected void spawnMeteorStrike(Hero hero) {
        // Спавнимо метеорит лише якщо текстури існують
        if (meteorWarningTexture != null && meteorExplosionTexture != null) {
            Vector2 targetPos = new Vector2(
                hero.getX(),
                hero.getY()
            );

            activeMeteorStrikes.add(new MeteorStrike(targetPos.x, targetPos.y, hero, gameMap, meteorWarningTexture, meteorExplosionTexture, meteorStrikeDamage));
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

    public void drawMeteorStrikes(SpriteBatch batch) {
        // Малюємо метеорити, лише якщо текстури існують
        if (meteorWarningTexture != null && meteorExplosionTexture != null) {
            for (MeteorStrike strike : activeMeteorStrikes) {
                strike.draw(batch);
            }
        }
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public String getStateName() {
        return state.name();
    }
}
