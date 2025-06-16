package com.mygdx.darkknight.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.levels.FightLevel;

import java.util.ArrayList;
import java.util.List;

public class ThirdBossFightLevel extends FightLevel {
    private Queen queenBoss;
    private boolean bossSpawned;

    public ThirdBossFightLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        // Додаємо параметри для метеоритів: кулдаун 5.0f, урон 15
        super(x, y, width, height, 5.0f, 15, Assets.meteorWarningTexture, Assets.meteorExplosionTexture);
        this.totalWaves = 1;
        this.bullets = bullets;
        this.gameMap = gameMap;
        this.enemiesToAdd = enemiesToAdd;
        this.currentWaveEnemies = new ArrayList<>();
        this.bossSpawned = false;
    }

    @Override
    public void update(float deltaTime, Hero hero, List<Enemy> globalEnemies) {
        // Синхронізуємо enemiesToAdd із globalEnemies і currentWaveEnemies
        if (!enemiesToAdd.isEmpty()) {
            globalEnemies.addAll(enemiesToAdd);
            currentWaveEnemies.addAll(enemiesToAdd);
            enemiesToAdd.clear();
        }

        // ВАЖЛИВО: викликаємо super.update() для оновлення метеоритів
        super.update(deltaTime, hero, globalEnemies);

        // Оновлюємо ворогів
        for (Enemy enemy : currentWaveEnemies) {
            enemy.update(hero, deltaTime);
        }

        // Оновлюємо кулі
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update(deltaTime, gameMap, globalEnemies);
            if (bullet.shouldRemove() || bullet.isStrike()) {
                bullets.remove(i);
            }
        }

        // Перевіряємо зіткнення куль із героєм
        for (Bullet bullet : bullets) {
            if (bullet.isOpponent() && bullet.getBoundingRectangle().overlaps(hero.getBoundingRectangle())) {
                Enemy owner = bullet.getEnemy();
                int damage = owner != null ? owner.getDamage() : 10;
                hero.takeDamage(damage, owner.getArmorIgnore());
                bullet.strike(owner, hero);
            }
        }

        if (bossSpawned && queenBoss != null && queenBoss.isDead() && currentWaveEnemies.isEmpty()) {
            state = LevelState.COMPLETED;
            gameMap.openDoors();
        }
    }

    @Override
    protected void spawnEnemies(List<Enemy> globalEnemies) {
        if (!bossSpawned) {
            // Спавн у центрі верхньої половини кімнати
            float halfHeight = roomArea.height / 2;
            float bossX = roomArea.x + roomArea.width / 2 - 48; // Центр по X
            float bossY = roomArea.y + roomArea.height - halfHeight / 2 - 48; // Центр верхньої половини по Y

            queenBoss = new Queen(bossX, bossY, gameMap, roomArea, bullets, currentWaveEnemies, enemiesToAdd);

            // Додаємо боса до списків
            currentWaveEnemies.add(queenBoss);
            globalEnemies.add(queenBoss);
            enemiesToAdd.add(queenBoss);

            bossSpawned = true;
        }
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        return null;
    }

    public boolean isLevelComplete() {
        boolean complete = bossSpawned && queenBoss != null && queenBoss.isDead() && currentWaveEnemies.isEmpty();
        return complete;
    }

    public Queen getBoss() {
        return queenBoss;
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }
}
