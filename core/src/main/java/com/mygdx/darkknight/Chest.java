package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.darkknight.effects.Effect;
import com.mygdx.darkknight.weapons.Weapon;

public class Chest {
    private float x, y;
    private Weapon weapon;
    private Effect effect;
    private boolean opened = false;
    private Texture texture;
    private boolean visible = true;

    public Chest(float x, float y, Weapon weapon) {
        this.x = x;
        this.y = 600 - y;
        this.weapon = weapon;
        this.effect = null;
    }

    public Chest(float x, float y, Effect effect) {
        this.x = x;
        this.y = 600 - y;
        this.effect = effect;
        this.weapon = null;
    }

    public Chest(float x, float y) {
        this.x = x;
        this.y = 600 - y;
        this.weapon = null;
        this.effect = null;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
        if (weapon != null) {
            this.effect = null; // щоб не було одночасно ефекту і зброї
        }
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
        if (effect != null) {
            this.weapon = null; // щоб не було одночасно ефекту і зброї
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(String texturePath) {
        if (this.texture != null) {
            this.texture.dispose(); // звільняємо стару текстуру, щоб не було витоку пам'яті
        }
        this.texture = new Texture(texturePath);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void draw(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, x * 32, (y - 1) * 32, 32, 32);
        }
    }

    public int getWidth() {
        return 32;
    }

    public int getHeight() {
        return 32;
    }
}
