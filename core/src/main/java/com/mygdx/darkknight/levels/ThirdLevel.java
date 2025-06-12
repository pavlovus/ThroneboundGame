package com.mygdx.darkknight.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.enemies.*;

import java.util.List;
import java.util.Random;

public class ThirdLevel extends FightLevel {

    public ThirdLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets) {
        super(x, y, width, height, 4.0f, 15, Assets.meteorWarningTexture, Assets.meteorExplosionTexture);
        this.maxEnemiesPerWave = 7; // Змінено з maxEnemies
        this.totalWaves = 5;

        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        float randomValue = (float) Math.random();

        if (randomValue <= 0.15f) { // 15%
            // Турель. Режим TURRET.AIMED стріляє по гравцю.
            Random random = new Random();
            int mode = random.nextInt(3);
            return new Turret(
                pos.x,
                pos.y,
                gameMap,
                this.roomArea,
                bullets,
                Turret.TurretMode.values()[mode]
            );
        } else if (randomValue <= 0.15f + 0.25f) { // 15% + 25% = 40%
            // Телепортер
            return new Teleporter(
                pos.x,
                pos.y,
                gameMap,
                this.roomArea
            );
        } else if (randomValue <= 0.15f + 0.25f + 0.30f) { // 40% + 30% = 70%
            return new Ghost(pos.x, pos.y, gameMap, this.roomArea);
        } else { // 70% + 30% = 100%
            // Ворог ближнього бою
            return new ShortAttackEnemy(
                Assets.shortEnemyTexture,
                pos.x,
                pos.y,
                20,
                30,
                200f,
                3,
                1,
                1.5f,
                gameMap, new ShortAttackAI(this.roomArea)
            );
        }
    }
}
