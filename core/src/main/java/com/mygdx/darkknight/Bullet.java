package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.enemies.Enemy;

public class Bullet {
    private static final float SPEED = 600f;
    private static final int WIDTH = 30;
    private static final int HEIGHT = 10;

    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private Texture texture;
    private boolean isOpponent;
    private Enemy enemy;
    private final Vector2 startPosition;
    private boolean remove;

    public Bullet(float startX, float startY, float angleDegrees, Texture texture, boolean isOpponent) {
        this.texture = texture;
        this.isOpponent = isOpponent;
        this.position = new Vector2(startX - WIDTH / 2f, startY - HEIGHT / 2f);
        this.angle = angleDegrees;
        float angleRadians = (float) Math.toRadians(angleDegrees);
        this.velocity = new Vector2((float) Math.cos(angleRadians), (float) Math.sin(angleRadians)).scl(SPEED);
        this.startPosition = new Vector2(startX, startY);
        this.remove = false;
    }

    public Bullet(float startX, float startY, float angleDegrees, Texture texture, boolean isOpponent, Enemy owner) {
        this.texture = texture;
        this.isOpponent = isOpponent;
        this.enemy = owner;
        this.position = new Vector2(startX - WIDTH / 2f, startY - HEIGHT / 2f);
        this.angle = angleDegrees;
        float angleRadians = (float) Math.toRadians(angleDegrees);
        this.velocity = new Vector2((float) Math.cos(angleRadians), (float) Math.sin(angleRadians)).scl(SPEED);
        this.startPosition = new Vector2(startX, startY);
    }

    public void update(float delta, GameMap map) {
        // Рух кулі
        float dx = (float) (SPEED * Math.cos(Math.toRadians(angle))) * delta;
        float dy = (float) (SPEED * Math.sin(Math.toRadians(angle))) * delta;
        Rectangle futureRect = new Rectangle(position.x + dx, position.y + dy, WIDTH, HEIGHT);
        if(!map.isCellBlocked(futureRect)) {
            position.x += dx;
            position.y += dy;
        } else {
            remove = true;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, WIDTH / 2f, HEIGHT / 2f, WIDTH, HEIGHT, 1, 1, angle, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x, position.y, WIDTH, HEIGHT);
    }

    public boolean isOpponent() {
        return isOpponent;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public boolean shouldRemove() {return remove;}
}
