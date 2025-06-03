package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemy {
    private Texture texture;
    private float x, y;
    private int width, height;
    private float speed;
    private int health;
    private int damage;
    private boolean dead;

    protected EnemyAI ai;

    private float attackCooldown = 0;
    private float attackTimer = 0;

    public Enemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, EnemyAI ai) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.health = health;
        this.damage = damage;
        this.ai = ai;
    }

    public abstract void attack(Hero hero);

    public void update(Hero hero, float delta) {
        if (ai != null) ai.update(this, hero, delta);
        attackTimer -= delta;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    public float getCenterX() {
        return x + width / 2f;
    }

    public float getCenterY() {
        return y + height / 2f;
    }

    public float distanceTo(Hero hero) {
        return Vector2.dst(getCenterX(), getCenterY(), hero.getCenterX(), hero.getCenterY());
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0){dead = true;}
    }

    public boolean canAttack() {
        return attackTimer <= 0;
    }

    public void resetAttackCooldown() {
        attackTimer = attackCooldown;
    }

    public void shootAt(float targetX, float targetY) {
        // TODO: spawn bullet toward target
    }

    // --- Getters and Setters ---
    public Rectangle getBoundingRectangle() {return new Rectangle(x, y, width, height);}
    public boolean isDead() {return dead;}
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float getSpeed() { return speed; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public void setAttackCooldown(float cooldown) { this.attackCooldown = cooldown; }
    public void dispose() { texture.dispose(); }
}
