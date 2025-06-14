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

public class FirstBossFightLevel extends FightLevel {
    private Butcher butcherBoss;
    private boolean bossSpawned = false;

    public FirstBossFightLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        super(x, y, width, height);
        this.totalWaves = 1;
        this.bullets = bullets;
        this.gameMap = gameMap;
        this.enemiesToAdd = enemiesToAdd;
        this.currentWaveEnemies = new ArrayList<>();
        Gdx.app.log("FirstBossFightLevel", "Initialized with roomArea: " + roomArea.toString() +
            ", gameMap: " + (gameMap != null) +
            ", bullets: " + (bullets != null) +
            ", enemiesToAdd: " + (enemiesToAdd != null));
    }

    @Override
    public void update(float deltaTime, Hero hero, List<Enemy> globalEnemies) {
        Gdx.app.log("FirstBossFightLevel", "Update called, State: " + state +
            ", Hero bounds: " + hero.getBoundingRectangle().toString() +
            ", Room area: " + roomArea.toString() +
            ", Hero in room: " + roomArea.contains(hero.getBoundingRectangle()) +
            ", Doors closed: " + gameMap.isDoorsClosed() +
            ", Boss spawned: " + bossSpawned +
            ", Current wave enemies count: " + currentWaveEnemies.size() +
            ", Global enemies count: " + globalEnemies.size());

        super.update(deltaTime, hero, globalEnemies);

        if (bossSpawned && butcherBoss != null && butcherBoss.isDead() && currentWaveEnemies.isEmpty()) {
            Gdx.app.log("FirstBossFightLevel", "Boss defeated, level complete");
        }
    }

    @Override
    protected void spawnEnemies(List<Enemy> globalEnemies) {
        Gdx.app.log("FirstBossFightLevel", "spawnEnemies called, bossSpawned: " + bossSpawned);

        if (!bossSpawned) {
            // Спавн у центрі лівого верхнього квадранта
            float quadrantWidth = roomArea.width / 2;
            float quadrantHeight = roomArea.height / 2;
            float bossX = roomArea.x + quadrantWidth / 2 - 48; // Центр лівого верхнього квадранта, 96 - розмір боса
            float bossY = roomArea.y + roomArea.height - quadrantHeight / 2 - 48;

            Gdx.app.log("FirstBossFightLevel", "Creating boss at position: (" + bossX + ", " + bossY + ")");

            butcherBoss = new Butcher(bossX, bossY, gameMap, roomArea, bullets, currentWaveEnemies, globalEnemies);

            // Додаємо боса до списків
            currentWaveEnemies.add(butcherBoss);
            globalEnemies.add(butcherBoss);
            enemiesToAdd.add(butcherBoss);

            bossSpawned = true;

            Gdx.app.log("FirstBossFightLevel", "Boss spawned successfully. " +
                "Current wave enemies: " + currentWaveEnemies.size() +
                ", Global enemies: " + globalEnemies.size() +
                ", Boss health: " + butcherBoss.getHealth());
        }
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        // Не використовується для босса
        return null;
    }

    public boolean isLevelComplete() {
        boolean complete = bossSpawned && butcherBoss != null && butcherBoss.isDead() && currentWaveEnemies.isEmpty();
        Gdx.app.log("FirstBossFightLevel", "isLevelComplete check: bossSpawned=" + bossSpawned +
            ", boss null=" + (butcherBoss == null) +
            ", boss dead=" + (butcherBoss != null ? butcherBoss.isDead() : "N/A") +
            ", enemies empty=" + currentWaveEnemies.isEmpty() +
            ", result=" + complete);
        return complete;
    }

    // Додаткові методи для дебагу
    public Butcher getBoss() {
        return butcherBoss;
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }
}
