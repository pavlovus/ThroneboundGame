package com.mygdx.darkknight.levels;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.enemies.*; // Імпортуємо всі класи ворогів
import com.mygdx.darkknight.enemies.EnemyType; // Імпортуємо глобальний EnemyType

import java.util.ArrayList;
import java.util.List;

public class SecondLevel extends FightLevel {

    private int id = 0; // Для відстеження ворога всередині хвилі

    public SecondLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        super(x, y, width, height);

        // maxEnemiesPerWave можна ігнорувати, оскільки getMaxEnemiesForCurrentWave буде динамічним
        this.totalWaves = 1; // TODO: змінити на 3

        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;
        this.enemiesToAdd = enemiesToAdd; // Цей список потрібен для Matriarch, щоб додавати нових ворогів до глобального списку

        this.levelEnemies = new EnemyType[][]{
            {EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK}, // Хвиля 1: 4× Пацюк
//            {EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK}, // Хвиля 2: 3× Пацюк + 2× Скелет
//            {EnemyType.MATRIARCH, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK}  // Хвиля 3: 1× Спавнер + 3× Пацюк
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
                return new LongAttackEnemy(Assets.long_1Texture, pos.x, pos.y, 40, 40, 80, 3, 1, 1, Assets.enemyBulletTexture, bullets, gameMap, new LongAttackAI(this.roomArea));
            case MATRIARCH:
                // Передаємо текстуру матки та текстуру міньйонів (пацюків)
                return new Matriarch(Assets.mom_1Texture, Assets.short_1Texture, pos.x, pos.y, gameMap, this.roomArea, currentWaveEnemies, enemiesToAdd, bullets);
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        }
    }
}
