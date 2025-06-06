package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Hero {
    private Texture texture;
    private float x, y;
    private final int width = 25, height = 32;
    private int speed = 600;
    private int maxHealth;
    private int maxArmor;
    private int health;
    private int armor;
    private boolean dead;
    private Rectangle bounds;

    public Hero(String texturePath, float x, float y, int health, int armor) {
        texture = new Texture(texturePath);
        this.x = x;
        this.y = y;
        dead = false;
        this.health = health;
        this.armor = armor;
        maxHealth = health;
        maxArmor = armor;
    }

    public void moveWithCollision(float dx, float dy, GameMap map) {
        Rectangle futureRect = new Rectangle(x + dx, y, width, height);
        if (!map.isCellBlocked(futureRect)) {
            x += dx;
        }

        futureRect.setPosition(x, y + dy);
        if (!map.isCellBlocked(futureRect)) {
            y += dy;
        }
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0){dead = true;}
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public float getCenterX() {
        return x + width / 2f;
    }

    public float getCenterY() {
        return y + height / 2f;
    }

    public void dispose() {
        texture.dispose();
    }

    public float getX() { return x; }

    public float getY() { return y; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getSpeed() {
        return speed;
    }

    public Rectangle getBoundingRectangle() {return new Rectangle(x, y, width, height);}

    public int getMaxHealth() {return maxHealth;}

    public int getMaxArmor() {return maxArmor;}

    public int getHealth() {return health;}

    public int getArmor() {return armor;}

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Vector2 getCenter() {
        return new Vector2(getCenterX(), getCenterY());
    }

    public boolean isDead(){return dead;}
}
