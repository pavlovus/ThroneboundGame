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
        Gdx.app.log("SecondBossFightLevel", "Initialized with roomArea: " + roomArea.toString() +
            ", gameMap: " + (gameMap != null) +
            ", bullets: " + (bullets != null) +
            ", enemiesToAdd: " + (enemiesToAdd != null));
    }

    @Override
    public void update(float deltaTime, Hero hero, List<Enemy> globalEnemies) {
        Gdx.app.log("SecondBossFightLevel", "Update called, State: " + state +
            ", Hero bounds: " + hero.getBoundingRectangle().toString() +
            ", Room area: " + roomArea.toString() +
            ", Hero in room: " + roomArea.contains(hero.getBoundingRectangle()) +
            ", Doors closed: " + gameMap.isDoorsClosed() +
            ", Boss spawned: " + bossSpawned +
            ", Current wave enemies count: " + currentWaveEnemies.size() +
            ", Global enemies count: " + globalEnemies.size());

        super.update(deltaTime, hero, globalEnemies);

        if (bossSpawned && jesterBoss != null && jesterBoss.isDead() && currentWaveEnemies.isEmpty()) {
            bossDefeated = true;
            state = LevelState.COMPLETED;
            gameMap.openDoors();
            Gdx.app.log("SecondBossFightLevel", "Boss defeated, level complete");
        }
    }

    @Override
    protected void spawnEnemies(List<Enemy> globalEnemies) {
        Gdx.app.log("SecondBossFightLevel", "spawnEnemies called, bossSpawned: " + bossSpawned);

        if (!bossSpawned) {
            // Спавн у центрі кімнати
            float bossX = roomArea.x + roomArea.width / 2 - 25; // Центр, 96 - розмір боса
            float bossY = roomArea.y + roomArea.height / 2 - 30;

            Gdx.app.log("SecondBossFightLevel", "Creating boss at position: (" + bossX + ", " + bossY + ")");

            jesterBoss = new Jester(bossX, bossY, gameMap, roomArea, bullets, currentWaveEnemies, enemiesToAdd, globalEnemies);

            // Додаємо боса до списків
            currentWaveEnemies.add(jesterBoss);
            globalEnemies.add(jesterBoss);
            enemiesToAdd.add(jesterBoss);

            bossSpawned = true;

            Gdx.app.log("SecondBossFightLevel", "Boss spawned successfully. " +
                "Current wave enemies: " + currentWaveEnemies.size() +
                ", Global enemies: " + globalEnemies.size() +
                ", Boss health: " + jesterBoss.getHealth());
        }
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        // Не використовується для боса
        return null;
    }
}
