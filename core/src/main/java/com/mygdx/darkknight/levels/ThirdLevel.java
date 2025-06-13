package com.mygdx.darkknight.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.*;

import java.util.List;

public class ThirdLevel extends FightLevel {

    public ThirdLevel(Hero hero, SpriteBatch batch, float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets) {
        super(hero, batch, gameMap, x, y, width, height);

        this.maxEnemiesPerWave = 7; // Збільшимо кількість ворогів
        this.totalWaves = 5; // Збільшимо кількість хвиль

        this.bulletTexture = Assets.enemyBulletTexture; // Використовуємо існуючу текстуру для куль
        this.bullets = bullets;
        this.gameMap = gameMap;
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        float randomValue = (float) Math.random();

        if (randomValue <= 0.15f) { // 15%
            // Турель. Режим TURRET.AIMED стріляє по гравцю.
            return new Turret(
                pos.x,
                pos.y,
                gameMap,
                this.roomArea,
                bullets,
                Turret.TurretMode.BURST
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
            // Метеорит
//            return new Meteor(
//                pos.x,
//                pos.y,
//                gameMap,
//                this.roomArea
//            );

            //TODO: повернути метеорити
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
