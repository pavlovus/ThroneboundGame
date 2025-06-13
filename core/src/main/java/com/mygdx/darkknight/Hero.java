package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.effects.Effect;
import com.mygdx.darkknight.weapons.Weapon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private List<Effect> activeEffects = new ArrayList<>();
    private Weapon currentWeapon;
    private List<Weapon> weapons = new ArrayList<>();

    public Hero(String texturePath, float x, float y, int health, int armor, Weapon weapon) {
        texture = new Texture(texturePath);
        this.x = x;
        this.y = y;
        dead = false;
        this.health = health;
        this.armor = armor;
        maxHealth = health;
        maxArmor = armor;
        this.currentWeapon = weapon;
        weapons.add(weapon);
    }

    public void moveWithCollision(float dx, float dy, GameMap map) {
        if (x < 4440 && 4340 < x && y < 4600 && 4539 < y) {
            x = 2378;
            y = 4921;
        }

        if (x < 5988 && 5851 < x && y < 9690 && 9640 < y) {
            x = 566;
            y = 10015;
        }
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

    public void heal(int heal) {
        health += heal;
    }

    public void addEffect(Effect effect) {
        activeEffects.add(effect);
    }

    public void updateEffects(float deltaTime) {
        for (int i = activeEffects.size() - 1; i >= 0; i--) {
            Effect effect = activeEffects.get(i);
            effect.update(this, deltaTime);
            if (effect.isExpired()) {
                activeEffects.remove(i);
            }
        }
    }

    public List<Effect> getActiveEffects() {
        return activeEffects;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public void addWeapon(Weapon... weaponList) {
        Collections.addAll(weapons, weaponList);
    }

    public List<Weapon> getWeapons() {
        return weapons;
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

    public Weapon getCurrentWeapon(){ return currentWeapon; }
    public void setCurrentWeapon(Weapon weapon){ this.currentWeapon = weapon; }
}
