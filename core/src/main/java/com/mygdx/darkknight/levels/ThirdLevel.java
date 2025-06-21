package com.mygdx.darkknight.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.enemies.*; // Зберігаємо цей імпорт для класів ворогів
import com.mygdx.darkknight.enemies.EnemyType; // !!! Додано імпорт для глобального EnemyType !!!

import java.util.ArrayList;
import java.util.List;

public class ThirdLevel extends FightLevel {

    private int id = 0; // Для відстеження ворога всередині хвилі
    private Music music;

    public ThirdLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        super(x, y, width, height);

        this.totalWaves = 4; // TODO: змінити на 4
        music = Gdx.audio.newMusic(Gdx.files.internal("pigDied.mp3"));
        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;
        this.enemiesToAdd = enemiesToAdd; // Цей список потрібен для Matriarch, щоб додавати нових ворогів до глобального списку

        // Визначення типів ворогів для кожної хвилі
        this.levelEnemies = new EnemyType[][]{
            {EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK}, // Хвиля 1: 3× Скелет + 3× Пацюк
            {EnemyType.MATRIARCH, EnemyType.MATRIARCH, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK}, // Хвиля 2: 2× Спавнер + 3× Скелет + 3× Пацюк
            {EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK}, // Хвиля 3: 5× Пацюк + 3× Скелет
            {EnemyType.MATRIARCH, EnemyType.MATRIARCH, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK, EnemyType.LONG_ATTACK} // Хвиля 4: 2× Спавнер + 5× Скелет
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
                return new LongAttackEnemy(Assets.long_1Texture ,pos.x, pos.y, 40, 40, 80, 3, 1, 1, Assets.long1AttackTexture, bullets, gameMap, new LongAttackAI(this.roomArea));
            case MATRIARCH:
                return new Matriarch(Assets.mom_1Texture, Assets.short_1Texture, pos.x, pos.y, gameMap, this.roomArea, currentWaveEnemies, enemiesToAdd, bullets);
            default:
                throw new IllegalArgumentException("Unknown enemy type: " + enemyType);
        }
    }
}
