package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.List;

public class Bullet {
    protected float speed;
    protected int width, height;
    protected Vector2 position;
    private Vector2 velocity;
    protected float angle;
    protected Texture texture;
    private boolean isOpponent;
    private Enemy enemy;
    private final Vector2 startPosition;
    protected boolean remove;

    public Bullet(float startX, float startY, float angleDegrees, Texture texture, boolean isOpponent, int width, int height, float speed) {
        this.texture = texture;
        this.speed = speed;
        this.height = height;
        this.width = width;
        this.isOpponent = isOpponent;
        this.position = new Vector2(startX - width / 2f, startY - height / 2f);
        this.angle = angleDegrees;
        float angleRadians = (float) Math.toRadians(angleDegrees);
        this.velocity = new Vector2((float) Math.cos(angleRadians), (float) Math.sin(angleRadians)).scl(speed);
        this.startPosition = new Vector2(startX, startY);
        this.remove = false;
    }

    public Bullet(float startX, float startY, float angleDegrees, Texture texture, boolean isOpponent, Enemy owner,  int width, int height, float speed) {
        this.texture = texture;
        this.isOpponent = isOpponent;
        this.enemy = owner;
        this.position = new Vector2(startX - width / 2f, startY - height / 2f);
        this.angle = angleDegrees;
        this.height = height;
        this.width = width;
        float angleRadians = (float) Math.toRadians(angleDegrees);
        this.velocity = new Vector2((float) Math.cos(angleRadians), (float) Math.sin(angleRadians)).scl(speed);
        this.startPosition = new Vector2(startX, startY);
        this.remove = false;
        this.speed = speed;
    }

    public void update(float delta, GameMap map, List<Enemy> enemies) {
        // Рух кулі
        float dx = (float) (speed * Math.cos(Math.toRadians(angle))) * delta;
        float dy = (float) (speed * Math.sin(Math.toRadians(angle))) * delta;
        Rectangle futureRect = new Rectangle(position.x + dx, position.y + dy, width, height);
        if(!map.isCellBlocked(futureRect)) {
            position.x += dx;
            position.y += dy;
        } else {
            remove = true;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, width / 2f, height / 2f, width, height, 1, 1, angle, 0, 0, texture.getWidth(), texture.getHeight(), false, false);
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public boolean isOpponent() {
        return isOpponent;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public boolean shouldRemove() {return remove;}

    public void setRemove(boolean remove) {this.remove = remove;}
}
