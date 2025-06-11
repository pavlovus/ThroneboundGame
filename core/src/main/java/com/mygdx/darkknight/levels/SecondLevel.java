package com.mygdx.darkknight.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.enemies.*;

import java.util.List;

public class SecondLevel extends FightLevel {

    public SecondLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets) {
        super(x, y, width, height);

        this.maxEnemiesPerWave = 6; // Збільшимо кількість ворогів, оскільки типів більше
        this.totalWaves = 4; // Збільшимо кількість хвиль

        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        float randomValue = (float) Math.random();

        if (randomValue <= 0.33f) {
            // Привид
            return new Ghost(
                pos.x,
                pos.y,
                gameMap,
                this.roomArea
            );
        } else if (randomValue <= 0.66f) {
            // Ворог дальнього бою
            return new LongAttackEnemy(
                Assets.longEnemyTexture,
                pos.x,
                pos.y,
                20,
                30,
                150f,
                2,
                1,
                1.5f,
                bulletTexture,
                bullets,
                gameMap,
                new LongAttackAI(this.roomArea)
            );
        } else {
            // Телепортер
            return new Teleporter(
                pos.x,
                pos.y,
                gameMap,
                this.roomArea
            );
        }
    }
}
