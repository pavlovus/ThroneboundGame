package com.mygdx.darkknight.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.*;
import com.mygdx.darkknight.enemies.EnemyType;
import com.mygdx.darkknight.enemies.Turret;

import java.util.ArrayList;
import java.util.List;

public class TenthLevel extends FightLevel {

    private int id = 0; // Для відстеження ворога всередині хвилі
    private final float[] meteorCooldowns = {6.0f, 5.5f, 5.0f, 4.0f, 3.0f}; // Кулдаун метеоритів для кожної хвилі

    public TenthLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        super(x, y, width, height, 6, 40, Assets.meteorWarningTexture, Assets.meteorExplosionTexture);

        this.totalWaves = 1; // TODO: змінити на 5


        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;
        this.enemiesToAdd = enemiesToAdd;

        // Визначення типів ворогів для кожної хвилі
        this.levelEnemies = new EnemyType[][]{
            // Хвиля 1: 2× TURRET_BURST + 2× TELEPORTER + 2× GHOST + 2× LONG_ATTACK
            {EnemyType.TURRET_BURST, EnemyType.TURRET_BURST, EnemyType.TELEPORTER, EnemyType.TELEPORTER, EnemyType.GHOST, EnemyType.GHOST, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK},
//            // Хвиля 2: 1× HEALER + 2× TURRET_RANDOM + 2× MATRIARCH + 2× SHORT_ATTACK
//            {EnemyType.HEALER, EnemyType.TURRET_RANDOM, EnemyType.TURRET_RANDOM, EnemyType.MATRIARCH, EnemyType.MATRIARCH, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK},
//            // Хвиля 3: 2× TURRET_BURST + 2× TELEPORTER + 3× GHOST + 2× LONG_ATTACK
//            {EnemyType.TURRET_BURST, EnemyType.TURRET_BURST, EnemyType.TELEPORTER, EnemyType.TELEPORTER, EnemyType.GHOST, EnemyType.GHOST, EnemyType.GHOST, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK},
//            // Хвиля 4: 2× HEALER + 2× TURRET_BURST + 2× MATRIARCH + 2× TELEPORTER
//            {EnemyType.HEALER, EnemyType.HEALER, EnemyType.TURRET_BURST, EnemyType.TURRET_BURST, EnemyType.MATRIARCH, EnemyType.MATRIARCH, EnemyType.TELEPORTER, EnemyType.TELEPORTER},
//            // Хвиля 5: 2× HEALER + 3× TURRET_BURST + 3× MATRIARCH + 2× TELEPORTER + 2× GHOST
//            {EnemyType.HEALER, EnemyType.HEALER, EnemyType.TURRET_BURST, EnemyType.TURRET_BURST, EnemyType.TURRET_BURST, EnemyType.MATRIARCH, EnemyType.MATRIARCH, EnemyType.MATRIARCH, EnemyType.TELEPORTER, EnemyType.TELEPORTER, EnemyType.GHOST, EnemyType.GHOST}
        };
    }

    @Override
    public void update(float deltaTime, Hero hero, List<Enemy> globalEnemies) {
        // Оновлюємо кулдаун метеоритів залежно від поточної хвилі
        if (currentWave > 0 && currentWave <= meteorCooldowns.length) {
            this.meteorStrikeSpawnCooldown = meteorCooldowns[currentWave - 1];
        }
        super.update(deltaTime, hero, globalEnemies);
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
                return new ShortAttackEnemy(Assets.short_3Texture, pos.x, pos.y, 40, 40, 120, 3, 1, 1, bullets, gameMap, new ShortAttackAI(this.roomArea));
            case LONG_ATTACK:
                return new LongAttackEnemy(Assets.long_3Texture, pos.x, pos.y, 40, 40, 80, 3, 1, 1, Assets.queenBulletTexture, bullets, gameMap, new LongAttackAI(this.roomArea));
            case HEALER:
                return new Healer(Assets.healer_3Texture, pos.x, pos.y, gameMap, this.roomArea, currentWaveEnemies, bullets);
            case TELEPORTER:
                return new Teleporter(Assets.teleporterTexture, pos.x, pos.y, gameMap, this.roomArea, bullets);
            case GHOST:
                return new Ghost(Assets.ghost_3Texture, pos.x, pos.y, gameMap, this.roomArea, bullets);
            case MATRIARCH:
                return new Matriarch(Assets.mom_3Texture, Assets.short_2Texture, pos.x, pos.y, gameMap, this.roomArea, currentWaveEnemies, enemiesToAdd, bullets);
            case TURRET_RANDOM:
                return new Turret(pos.x, pos.y, gameMap, this.roomArea, bullets, Turret.TurretMode.RANDOM);
            case TURRET_BURST:
                return new Turret(pos.x, pos.y, gameMap, this.roomArea, bullets, Turret.TurretMode.BURST);
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        }
    }
}
