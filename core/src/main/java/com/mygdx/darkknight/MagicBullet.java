package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.weapons.Weapon;

import java.util.List;

public class MagicBullet extends Bullet {
    private float timeToLive;
    private float aliveTime;
    private Animation<TextureRegion> explosionAnimation;
    private float explosionTime = 0f;
    private boolean exploded = false;
    private Vector2 explosionPosition;
    private float explosionSize;

    public MagicBullet(float startX, float startY, float angleDegrees, Texture texture, String animationTexturePath, boolean isOpponent, int width, int height, float speed, float timeToLive, float explosionSize, Weapon weapon) {
        super(startX, startY, angleDegrees, texture,animationTexturePath, isOpponent, width, height, speed, weapon);
        this.timeToLive = timeToLive;
        this.aliveTime = 0.0f;
        this.explosionSize = explosionSize;
        initExplosionAnimation();
    }

    public void update(float delta, GameMap map, List<Enemy> enemies) {
        if (exploded) {
            explosionTime += delta;
            if (explosionAnimation.isAnimationFinished(explosionTime)) {
                remove = true;
            }
            return;
        }

        if (timeToLive > 0f) {
            aliveTime += delta;
            if (aliveTime >= timeToLive) {
                explode(enemies);
                return;
            }
        }

        float dx = (float) (speed * Math.cos(Math.toRadians(angle))) * delta;
        float dy = (float) (speed * Math.sin(Math.toRadians(angle))) * delta;
        Rectangle futureRect = new Rectangle(position.x + dx, position.y + dy, width, height);
        if (!map.isCellBlocked(futureRect)) {
            position.x += dx;
            position.y += dy;
        } else {
            explode(enemies);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (exploded && explosionAnimation != null) {
            TextureRegion currentFrame = explosionAnimation.getKeyFrame(explosionTime, false);
            batch.draw(currentFrame, explosionPosition.x, explosionPosition.y);
        } else {
            batch.draw(texture, position.x, position.y, width / 2f, height / 2f,
                width, height, 1, 1, angle, 0, 0,
                texture.getWidth(), texture.getHeight(), false, false);
        }
    }

    public void explode(List<Enemy> enemies) {
        exploded = true;
        explosionTime = 0f;

        float halfSize = explosionSize / 2f;

        float centerX = position.x + width / 2f;
        float centerY = position.y + height / 2f;

        explosionPosition = new Vector2(centerX, centerY);

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

    private void initExplosionAnimation() {
        Texture sheet = animationTexture;
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);
        TextureRegion[] frames = new TextureRegion[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            frames[i] = tmp[i][0];
        }
        explosionAnimation = new Animation<>(0.1f, frames);
    }

    public boolean isExploded() {
        return exploded;
    }
}

