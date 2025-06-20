package com.mygdx.darkknight.bosses;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.levels.FightLevel;

import java.util.ArrayList;
import java.util.List;

public class SecondBossFightLevel extends FightLevel {
    private Jester jesterBoss;
    private boolean bossSpawned;
    private boolean bossDefeated;

    public SecondBossFightLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        super(x, y, width, height);
        this.totalWaves = 1;
        this.bullets = bullets;
        this.gameMap = gameMap;
        this.enemiesToAdd = enemiesToAdd;
        this.currentWaveEnemies = new ArrayList<>();
        this.bossSpawned = false;
        this.bossDefeated = false;
    }

    @Override
    public void update(float deltaTime, Hero hero, List<Enemy> globalEnemies) {
        super.update(deltaTime, hero, globalEnemies);

        // Check if boss is defeated but don't override the parent state machine
        if (bossSpawned && jesterBoss != null && jesterBoss.isDead() && currentWaveEnemies.isEmpty() && !bossDefeated) {
            bossDefeated = true;
            // Let the parent class handle the proper state transition to WAITING_FOR_DOOR_OPEN -> COMPLETED
            // The parent FightLevel will automatically transition to WAITING_FOR_DOOR_OPEN when currentWaveEnemies is empty
        }
    }

    @Override
    protected void spawnEnemies(List<Enemy> globalEnemies) {
        if (!bossSpawned) {
            // Спавн у центрі кімнати
            float bossX = roomArea.x + roomArea.width / 2 - 25; // Центр, 96 - розмір боса
            float bossY = roomArea.y + roomArea.height / 2 - 30;

            jesterBoss = new Jester(bossX, bossY, gameMap, roomArea, bullets, currentWaveEnemies, enemiesToAdd, globalEnemies);

            // Додаємо боса до списків
            currentWaveEnemies.add(jesterBoss);
            globalEnemies.add(jesterBoss);
            enemiesToAdd.add(jesterBoss);

            bossSpawned = true;
        }
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        // Не використовується для боса
        return null;
    }
}
