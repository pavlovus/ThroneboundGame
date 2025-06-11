package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.weapons.Weapon;

import java.util.List;

public class MagicBullet extends Bullet {
    private float timeToLive;
    private float aliveTime;
    private Weapon weapon;

    public MagicBullet(float startX, float startY, float angleDegrees, Texture texture, boolean isOpponent, int width, int height, float speed, float timeToLive, Weapon weapon) {
        super(startX, startY, angleDegrees, texture, isOpponent, width, height, speed);
        this.timeToLive = timeToLive;
        this.aliveTime = 0.0f;
        this.weapon = weapon;
    }

    public void update(float delta, GameMap map, List<Enemy> enemies) {
        if (timeToLive > 0f) {
            aliveTime += delta;
            if (aliveTime >= timeToLive) {
                remove = true;
                explode(enemies);
                return;
            }
        }
        // Рух кулі
        float dx = (float) (speed * Math.cos(Math.toRadians(angle))) * delta;
        float dy = (float) (speed * Math.sin(Math.toRadians(angle))) * delta;
        Rectangle futureRect = new Rectangle(position.x + dx, position.y + dy, width, height);
        if(!map.isCellBlocked(futureRect)) {
            position.x += dx;
            position.y += dy;
        } else {
            remove = true;
            explode(enemies);
        }
    }

    public void explode(List<Enemy> enemies) {
        float explosionSize = 32f;
        float halfSize = explosionSize / 2f;

        // Центр кулі
        float centerX = position.x + width / 2f;
        float centerY = position.y + height / 2f;

        Rectangle explosionArea = new Rectangle(
            centerX - halfSize,
            centerY - halfSize,
            explosionSize,
            explosionSize
        );

        for (Enemy enemy : enemies) {
            if (explosionArea.overlaps(enemy.getBoundingRectangle())) {
                enemy.takeDamage(weapon.getDamage());
            }
        }
    }
}

