package com.mygdx.darkknight.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.List;

public abstract class Weapon {
    private Texture texture;
    private int damage;
    private float angle = 0;
    private final int width, height;
    private String name;
    private int bonusDamage = 0;

    public Weapon(String texturePath, int damage, int width, int height) {
        texture = new Texture(texturePath);
        this.damage = damage;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(SpriteBatch batch, float centerX, float centerY, boolean flip);
    public abstract void attack(Hero hero, List<Bullet> bullets, List<Enemy> enemies);
    public abstract void update(float deltaTime, Hero hero);

    public void updateAngle(float mouseX, float mouseY, float heroX, float heroY) {
        if (!(this instanceof AxeWeapon)) {
            float dx = mouseX - heroX;
            float dy = mouseY - heroY;
            angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        }
    }

    public void addDamageBonus(int bonus) {
        bonusDamage += bonus;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void dispose() {
        texture.dispose();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDamage() {
        return Math.max(1, damage + bonusDamage);
    }

    public void setDamage(int damage) {this.damage = damage;}

    public Texture getTexture(){ return texture;}

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

