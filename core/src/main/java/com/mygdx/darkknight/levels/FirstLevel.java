package com.mygdx.darkknight.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.*;

import java.util.ArrayList;
import java.util.List;

public class FirstLevel extends FightLevel {

    public FirstLevel(Hero hero, SpriteBatch batch, float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets) {
        super(hero, batch, gameMap, x, y, width, height);

        this.maxEnemiesPerWave = 1;
        this.totalWaves = 1;

        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;

    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        float randomValue = (float) Math.random();

        return new Turret(
            pos.x,
            pos.y,
            gameMap,
            this.roomArea,
            bullets,
            Turret.TurretMode.ROTATING
        );

//        if (randomValue <= 0.5f) {
//            return new ShortAttackEnemy(
//                Assets.shortEnemyTexture,
//                pos.x,
//                pos.y,
//                20,
//                30,
//                200f,
//                3,
//                1,
//                1.5f,
//                gameMap, new ShortAttackAI(this.roomArea)
//            );
//        } else {
//            return new LongAttackEnemy(
//                Assets.longEnemyTexture,
//                pos.x,
//                pos.y,
//                20,
//                30,
//                150f,
//                2,
//                1,
//                1.5f,
//                bulletTexture,
//                bullets,
//                gameMap,
//                new LongAttackAI(this.roomArea)
//            );
//        }
    }
}
