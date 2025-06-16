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
    }

    @Override
    public void update(float deltaTime, Hero hero, List<Enemy> globalEnemies) {
        super.update(deltaTime, hero, globalEnemies);
    }

    @Override
    protected void spawnEnemies(List<Enemy> globalEnemies) {
        if (!bossSpawned) {
            // Спавн у центрі лівого верхнього квадранта
            float quadrantWidth = roomArea.width / 2;
            float quadrantHeight = roomArea.height / 2;
            float bossX = roomArea.x + quadrantWidth / 2 - 48; // Центр лівого верхнього квадранта, 96 - розмір боса
            float bossY = roomArea.y + roomArea.height - quadrantHeight / 2 - 48;

            butcherBoss = new Butcher(bossX, bossY, gameMap, roomArea, bullets, currentWaveEnemies, globalEnemies);

            // Додаємо боса до списків
            currentWaveEnemies.add(butcherBoss);
            globalEnemies.add(butcherBoss);
            enemiesToAdd.add(butcherBoss);

            bossSpawned = true;
        }
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        return null;
    }

    public boolean isLevelComplete() {
        boolean complete = bossSpawned && butcherBoss != null && butcherBoss.isDead() && currentWaveEnemies.isEmpty();
        return complete;
    }
}
