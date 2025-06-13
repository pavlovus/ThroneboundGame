package com.mygdx.darkknight.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.enemies.*;
import com.mygdx.darkknight.enemies.EnemyType;
import com.mygdx.darkknight.enemies.Turret;

import java.util.ArrayList;
import java.util.List;

public class FourthLevel extends FightLevel {

    private int id = 0; // Для відстеження ворога всередині хвилі

    public FourthLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        super(x, y, width, height);

        this.totalWaves = 3; // Три хвилі

        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;
        this.enemiesToAdd = enemiesToAdd;

        // Визначення типів ворогів для кожної хвилі
        this.levelEnemies = new EnemyType[][]{
            // Хвиля 1: 4× LONG_ATTACK + 1× Turret (AIMED)
            {EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.TURRET_AIMED},
//            // Хвиля 2: 2× Turret (1 AIMED, 1 ROTATING) + 3× SHORT_ATTACK
//            {EnemyType.TURRET_AIMED, EnemyType.TURRET_ROTATING, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK},
//            // Хвиля 3: 2× Turret (1 RANDOM, 1 BURST) + 1× HEALER + 3× SHORT_ATTACK
//            {EnemyType.TURRET_RANDOM, EnemyType.TURRET_BURST, EnemyType.HEALER, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK}
        };
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        if (id >= levelEnemies[currentWave - 1].length) {
            id = 0;
        }

        EnemyType enemyType = levelEnemies[currentWave - 1][id];
        id++;

        switch (enemyType) {
            case SHORT_ATTACK:
                return new ShortAttackEnemy(Assets.short_2Texture, pos.x, pos.y, 40, 40, 120, 3, 1, 1, gameMap, new ShortAttackAI(this.roomArea));
            case LONG_ATTACK:
                return new LongAttackEnemy(Assets.long_2Texture, pos.x, pos.y, 40, 40, 80, 3, 1, 1, Assets.enemyBulletTexture, bullets, gameMap, new LongAttackAI(this.roomArea));
            case HEALER:
                return new Healer(Assets.healer_2Texture ,pos.x, pos.y, gameMap, this.roomArea, currentWaveEnemies);
            case TURRET_AIMED:
                return new Turret(pos.x, pos.y, gameMap, this.roomArea, bullets, Turret.TurretMode.AIMED);
            case TURRET_ROTATING:
                return new Turret(pos.x, pos.y, gameMap, this.roomArea, bullets, Turret.TurretMode.ROTATING);
            case TURRET_RANDOM:
                return new Turret(pos.x, pos.y, gameMap, this.roomArea, bullets, Turret.TurretMode.RANDOM);
            case TURRET_BURST:
                return new Turret(pos.x, pos.y, gameMap, this.roomArea, bullets, Turret.TurretMode.BURST);
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        }
    }
}
