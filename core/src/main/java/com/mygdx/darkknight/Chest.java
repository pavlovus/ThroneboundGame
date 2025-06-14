package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.darkknight.weapons.Weapon;

import java.util.List;

public class Chest {
    private float x, y;
    private int width, height = 32;
    private Weapon weapon;
    private boolean opened = false;
    private Texture texture;
    private boolean visible = false;


    public Chest(float x, float y, Weapon contents) {
        this.x = x;
        this.y = 600 - y;
        this.weapon = contents;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public boolean isOpened() {
        return opened;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void setTexture(String texturePath) {
        texture = new Texture(texturePath);
        this.texture = texture;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x * 32, (y - 1) * 32, 32, 32);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
