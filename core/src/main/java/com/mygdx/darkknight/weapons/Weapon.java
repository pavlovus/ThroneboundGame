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
        if(!(this instanceof AxeWeapon)){
            float dx = mouseX - (heroX + width / 2f);
            float dy = mouseY - (heroY - height / 2f);
            angle = (float) Math.toDegrees(Math.atan2(dy, dx));
        }
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

    public int getDamage() {return damage;}

    public void setDamage(int damage) {this.damage = damage;}

    public Texture getTexture(){ return texture;}
}

