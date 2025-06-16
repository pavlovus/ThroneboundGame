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

public class FifthLevel extends FightLevel {

    private int id = 0; // Для відстеження ворога всередині хвилі

    public FifthLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        super(x, y, width, height);

        this.totalWaves = 4; // TODO: змінити на 4


        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;
        this.enemiesToAdd = enemiesToAdd;

        // Визначення типів ворогів для кожної хвилі
        this.levelEnemies = new EnemyType[][]{
            // Хвиля 1: 2× TELEPORTER + 1× TURRET_AIMED + 3× SHORT_ATTACK
            {EnemyType.TELEPORTER, EnemyType.TELEPORTER, EnemyType.TURRET_AIMED, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK},
            // Хвиля 2: 1× TURRET_ROTATING + 1× TURRET_RANDOM + 2× LONG_ATTACK + 2× SHORT_ATTACK
            {EnemyType.TURRET_ROTATING, EnemyType.TURRET_RANDOM, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK},
            // Хвиля 3: 1× HEALER + 1× TURRET_RANDOM + 1× TURRET_BURST + 1× TELEPORTER + 2× LONG_ATTACK
            {EnemyType.HEALER, EnemyType.TURRET_RANDOM, EnemyType.TURRET_BURST, EnemyType.TELEPORTER, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK},
            // Хвиля 4: 1× TURRET_BURST + 1× TURRET_RANDOM + 2× TELEPORTER + 2× LONG_ATTACK
            {EnemyType.TURRET_BURST, EnemyType.TURRET_RANDOM, EnemyType.TELEPORTER, EnemyType.TELEPORTER, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK}
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
                return new ShortAttackEnemy(Assets.short_2Texture, pos.x, pos.y, 40, 40, 120, 3, 1, 1, bullets, gameMap, new ShortAttackAI(this.roomArea));
            case LONG_ATTACK:
                return new LongAttackEnemy(Assets.long_2Texture, pos.x, pos.y, 40, 40, 80, 3, 1, 1, Assets.long2AttackTexture, bullets, gameMap, new LongAttackAI(this.roomArea));
            case HEALER:
                return new Healer(Assets.healer_2Texture, pos.x, pos.y, gameMap, this.roomArea, currentWaveEnemies, bullets);
            case TELEPORTER:
                return new Teleporter(Assets.teleporterTexture, pos.x, pos.y, gameMap, this.roomArea, bullets);
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
