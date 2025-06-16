package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private int speed = 1500;
    private int maxHealth;
    private int maxArmor;
    private int health;
    private int armor;
    private boolean dead;
    private Rectangle bounds;
    private List<Effect> activeEffects = new ArrayList<>();
    private Weapon currentWeapon;
    private List<Weapon> weapons = new ArrayList<>();
    private BitmapFont damageFont;
    private List<DamageIndicator> damageIndicators = new ArrayList<>();
    private boolean nextIsRight = true;

    private static class DamageIndicator {
        String text;
        float timer;
        float yOffset;
        float alpha;
        boolean isRight;
        int damage;

        DamageIndicator(String text, boolean isRight, int damage) {
            this.text = text;
            this.timer = 1.0f;
            this.yOffset = 0;
            this.alpha = 1.0f;
            this.isRight = isRight;
            this.damage = damage;
        }
    }

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
        damageFont = new BitmapFont(Gdx.files.internal("assets/medievalLightFontSmaller.fnt"));
        damageFont.getData().setScale(1.0f);
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

    public void takeDamage(int dmg, boolean shieldIgnore) {
        if (shieldIgnore) {
            health -= dmg;
            if (health <= 0) {
                dead = true;
                health = 0;
            }
        } else {
            if (armor > 0) {
                armor -= dmg;
            } else {
                health -= dmg;
                if (health <= 0) {
                    dead = true;
                    health = 0;
                }
            }
        }
        damageIndicators.add(new DamageIndicator("-" + dmg, nextIsRight, dmg));
        nextIsRight = !nextIsRight;
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
        for (int i = damageIndicators.size() - 1; i >= 0; i--) {
            DamageIndicator indicator = damageIndicators.get(i);
            indicator.timer -= deltaTime;
            indicator.yOffset += 30 * deltaTime;
            indicator.alpha = Math.max(0, indicator.timer);
            if (indicator.timer <= 0) {
                damageIndicators.remove(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        GlyphLayout layout = new GlyphLayout();

        batch.draw(texture, x, y, width, height);
        for (DamageIndicator indicator : damageIndicators) {
            layout.setText(damageFont, indicator.text);
            float saturation = 0.5f - Math.min(indicator.damage / 30.0f, 0.5f);
            damageFont.setColor(1.0f, saturation, saturation, indicator.alpha);
            float textX = indicator.isRight ? x + width : x - layout.width * 2;
            textX += 13;
            float textY = y + height + indicator.yOffset;
            damageFont.draw(batch, indicator.text, textX, textY);
        }
        damageFont.setColor(1f, 1f, 1f, 1f);
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
        damageFont.dispose();
    }

    public float getX() { return x; }

    public float getY() { return y; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public int getSpeed() { return speed; }

    public Rectangle getBoundingRectangle() { return new Rectangle(x, y, width, height); }

    public int getMaxHealth() { return maxHealth; }

    public int getMaxArmor() { return maxArmor; }

    public int getHealth() { return health; }

    public int getArmor() { return armor; }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Vector2 getCenter() {
        return new Vector2(getCenterX(), getCenterY());
    }

    public boolean isDead() { return dead; }

    public Weapon getCurrentWeapon() { return currentWeapon; }

    public void setCurrentWeapon(Weapon weapon) { this.currentWeapon = weapon; }

    public List<Effect> getActiveEffects() {
        return activeEffects;
    }
}
