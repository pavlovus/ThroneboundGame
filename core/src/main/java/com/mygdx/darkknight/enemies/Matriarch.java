package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Texture; // Імпортуємо Texture
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.nio.Buffer;
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

    private Texture minionTexture; // Нове поле для текстури міньйонів

    // Конструктор тепер приймає текстури
    public Matriarch(Texture matriarchTexture, Texture minionTexture, float x, float y, GameMap gameMap, Rectangle roomBounds, List<Enemy> enemies, List<Enemy> enemiesToAdd, List<Bullet> bullets) {
        // Передаємо текстуру матки до батьківського конструктора Enemy
        super(matriarchTexture, x, y, 40, 40, 80f, 5, 0, bullets, new MatriarchAI(roomBounds, new Vector2(x,y)), gameMap, false);
        this.setAttackCooldown(SPAWN_COOLDOWN);
        this.spawnTimer = SPAWN_COOLDOWN;
        this.enemies = enemies;
        this.enemiesToAdd = enemiesToAdd;
        this.roomBounds = roomBounds;
        this.gameMap = gameMap;
        this.random = new Random();
        this.minionTexture = minionTexture; // Зберігаємо текстуру міньйонів
    }

    @Override
    public void attack(Hero hero) {
        // Матріарх не атакує героя безпосередньо, вона спавнить міньйонів.
        // Цей метод залишаємо порожнім або реалізуємо логіку атаки, якщо вона буде.
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);

        spawnTimer -= delta;
        if (spawnTimer <= 0) {
            spawnMinion();
            spawnTimer = SPAWN_COOLDOWN;
        }
    }

    private void spawnMinion() {
        if (getAliveMinionsCount() >= MAX_MINIONS_PER_MATRIARCH) {
            return; // Не спавнити, якщо досягнуто ліміту
        }

        Vector2 spawnPos = findValidSpawnPosition();
        if (spawnPos == null) {
            return; // Пропускаємо спавн, якщо не знайдено валідної позиції
        }

        // Використовуємо this.minionTexture для створення міньйона
        ShortAttackEnemy minion = new ShortAttackEnemy(
            this.minionTexture, // Передаємо текстуру міньйона, отриману через конструктор
            spawnPos.x, spawnPos.y,
            32, 32,
            180f,
            3,
            1,
            0.8f,
            bullets,
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

            // Обмежуємо координати межами кімнати, використовуючи розмір текстури міньйона
            spawnX = Math.max(roomBounds.x, Math.min(spawnX, roomBounds.x + roomBounds.width - minionTexture.getWidth()));
            spawnY = Math.max(roomBounds.y, Math.min(spawnY, roomBounds.y + roomBounds.height - minionTexture.getHeight()));

            // Перевіряємо, чи позиція валідна
            Rectangle testArea = new Rectangle(spawnX, spawnY, 32, 32); // Розмір міньйона, припускаємо 32х32 як стандарт
            if (!gameMap.isCellBlocked(testArea)) {
                return new Vector2(spawnX, spawnY);
            }
        }
        return null;
    }

    private int getAliveMinionsCount() {
        int count = 0;
        for (Enemy enemy : enemies) {
            // Перевіряємо, чи це екземпляр ShortAttackEnemy (або іншого типу міньйона)
            // і чи він не мертвий, і чи він не сама Матріарх (для уникнення помилок)
            if (enemy instanceof ShortAttackEnemy && !enemy.isDead()) {
                count++;
            }
        }
        return count;
    }
}
