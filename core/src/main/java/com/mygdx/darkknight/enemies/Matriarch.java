package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.util.List;
import java.util.Random;

public class Matriarch extends Enemy {
    private static final float SPAWN_COOLDOWN = 4.0f;
    private static final int MAX_MINIONS_PER_MATRIARCH = 5;
    private static final int MAX_SPAWN_ATTEMPTS = 10; // Максимальна кількість спроб для пошуку валідної позиції
    private static final float SPAWN_RADIUS = 20f; // Радіус спавну навколо Matriarch

    private float spawnTimer;
    private List<Enemy> enemies;
    private List<Enemy> enemiesToAdd;
    private Rectangle roomBounds;
    private GameMap gameMap;
    private Random random;

    public Matriarch(float x, float y, GameMap gameMap, Rectangle roomBounds, List<Enemy> enemies, List<Enemy> enemiesToAdd) {
        super(Assets.matriarchTexture, x, y, 32, 32, 60f, 5, 0, new MatriarchAI(roomBounds, new Vector2(x,y)), gameMap);
        this.setAttackCooldown(SPAWN_COOLDOWN);
        this.spawnTimer = SPAWN_COOLDOWN;
        this.enemies = enemies;
        this.enemiesToAdd = enemiesToAdd;
        this.roomBounds = roomBounds;
        this.gameMap = gameMap;
        this.random = new Random();
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);

        spawnTimer += delta;

        long minionCount = enemies.stream()
            .filter(e -> e instanceof ShortAttackEnemy && !(e instanceof Matriarch))
            .count();

        if (spawnTimer >= SPAWN_COOLDOWN && minionCount < MAX_MINIONS_PER_MATRIARCH) {
            spawnMinion();
            spawnTimer = 0;
        }
    }

    @Override
    public void attack(Hero hero) {
        // Matriarch не атакує безпосередньо
    }

    private void spawnMinion() {
        Vector2 spawnPos = findValidSpawnPosition();
        if (spawnPos == null) {
            return; // Пропускаємо спавн, якщо не знайдено валідної позиції
        }

        ShortAttackEnemy minion = new ShortAttackEnemy(
            Assets.shortEnemyTexture,
            spawnPos.x, spawnPos.y,
            32, 32,
            180f,
            3,
            1,
            0.8f,
            gameMap,
            new ShortAttackAI(roomBounds)
        );
        enemies.add(minion); // Додаємо до currentWaveEnemies
        enemiesToAdd.add(minion); // Додаємо до тимчасового списку для globalEnemies
    }

    private Vector2 findValidSpawnPosition() {
        for (int i = 0; i < MAX_SPAWN_ATTEMPTS; i++) {
            float spawnX = getCenterX() + (random.nextFloat() * 2 - 1) * SPAWN_RADIUS;
            float spawnY = getCenterY() + (random.nextFloat() * 2 - 1) * SPAWN_RADIUS;

            // Обмежуємо координати межами кімнати
            spawnX = Math.max(roomBounds.x, Math.min(spawnX, roomBounds.x + roomBounds.width - Assets.shortEnemyTexture.getWidth()));
            spawnY = Math.max(roomBounds.y, Math.min(spawnY, roomBounds.y + roomBounds.height - Assets.shortEnemyTexture.getHeight()));

            // Перевіряємо, чи позиція валідна
            Rectangle testArea = new Rectangle(spawnX, spawnY, 32, 32); // Розмір міньйона
            if (!gameMap.isCellBlocked(testArea)) {
                return new Vector2(spawnX, spawnY);
            }
        }
        return null; // Не знайдено валідної позиції після всіх спроб
    }
}
