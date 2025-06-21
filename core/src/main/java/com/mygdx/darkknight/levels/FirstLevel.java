package com.mygdx.darkknight.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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

    private Music music;
    public FirstLevel(float x, float y, float width, float height, GameMap gameMap, List<Bullet> bullets, List<Enemy> enemiesToAdd) {
        super(x, y, width, height);
        music = Gdx.audio.newMusic(Gdx.files.internal("pigDied.mp3"));
        this.totalWaves = 2;

        this.bulletTexture = Assets.enemyBulletTexture;
        this.bullets = bullets;
        this.gameMap = gameMap;

        this.levelEnemies = new EnemyType[][]{
            {EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK},
            {EnemyType.SHORT_ATTACK, EnemyType.SHORT_ATTACK, EnemyType.LONG_ATTACK}
        };
    }

    @Override
    protected Enemy createEnemy(Vector2 pos) {
        int current = id;
        id++;
        if (id == levelEnemies[currentWave - 1].length)
            id = 0;
        switch (levelEnemies[currentWave - 1][current]) {
            case EnemyType.SHORT_ATTACK:
                return new ShortAttackEnemy(Assets.short_1Texture, pos.x, pos.y, 40, 40, 120, 3, 1, 1, bullets, gameMap, new ShortAttackAI(this.roomArea));
            case EnemyType.LONG_ATTACK:
                return new LongAttackEnemy(Assets.long_1Texture, pos.x, pos.y, 40, 40, 80, 3, 1, 1, Assets.long1AttackTexture, bullets, gameMap, new LongAttackAI(this.roomArea));
            default:
                throw new IllegalArgumentException("Unknown enemy type");
        }
    }
}
