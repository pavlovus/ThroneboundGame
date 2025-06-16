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

public class SeventhLevel extends FightLevel {

    private int id = 0; // Для відстеження ворога всередині хвилі

    public SeventhLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        super(x, y, width, height);

        this.totalWaves = 4; // TODO: змінити на 4

        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;
        this.enemiesToAdd = enemiesToAdd;

        // Визначення типів ворогів для кожної хвилі
        this.levelEnemies = new EnemyType[][]{
            // Хвиля 1: 1× TURRET_AIMED + 1× TURRET_ROTATING + 1× TELEPORTER + 2× GHOST
            {EnemyType.TURRET_AIMED, EnemyType.TURRET_ROTATING, EnemyType.TELEPORTER, EnemyType.GHOST, EnemyType.GHOST},
            // Хвиля 2: 1× TURRET_RANDOM + 1× TURRET_BURST + 2× TELEPORTER + 1× HEALER + 1× MATRIARCH
            {EnemyType.TURRET_RANDOM, EnemyType.TURRET_BURST, EnemyType.TELEPORTER, EnemyType.TELEPORTER, EnemyType.HEALER, EnemyType.MATRIARCH},
            // Хвиля 3: 1× HEALER + 1× TURRET_ROTATING + 3× GHOST + 3× LONG_ATTACK
            {EnemyType.HEALER, EnemyType.TURRET_ROTATING, EnemyType.GHOST, EnemyType.GHOST, EnemyType.GHOST, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK},
            // Хвиля 4: 1× TURRET_RANDOM + 1× TURRET_BURST + 2× GHOST + 2× TELEPORTER + 2× MATRIARCH
            {EnemyType.TURRET_RANDOM, EnemyType.TURRET_BURST, EnemyType.GHOST, EnemyType.GHOST, EnemyType.TELEPORTER, EnemyType.TELEPORTER, EnemyType.MATRIARCH, EnemyType.MATRIARCH}
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
                return new ShortAttackEnemy(Assets.short_1Texture, pos.x, pos.y, 40, 40, 120, 3, 1, 1, bullets, gameMap, new ShortAttackAI(this.roomArea));
            case LONG_ATTACK:
                return new LongAttackEnemy(Assets.long_1Texture, pos.x, pos.y, 40, 40, 80, 3, 1, 1, Assets.long2AttackTexture, bullets, gameMap, new LongAttackAI(this.roomArea));
            case HEALER:
                return new Healer(Assets.healer_2Texture, pos.x, pos.y, gameMap, this.roomArea, currentWaveEnemies, bullets);
            case TELEPORTER:
                return new Teleporter(Assets.teleporterTexture, pos.x, pos.y, gameMap, this.roomArea, bullets);
            case GHOST:
                return new Ghost(Assets.ghostEnemyTexture, pos.x, pos.y, gameMap, this.roomArea, bullets);
            case MATRIARCH:
                return new Matriarch(Assets.mom_2Texture, Assets.short_2Texture, pos.x, pos.y, gameMap, this.roomArea, currentWaveEnemies, enemiesToAdd, bullets);
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
