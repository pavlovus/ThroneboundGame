package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Hero {
    private Texture texture;
    private float x, y;
    private final int width = 150, height = 150;
    private int speed = 600;
    private Rectangle bounds;

    public Hero(String texturePath, float screenWidth, float screenHeight) {
        texture = new Texture(texturePath);
        x = screenWidth / 2f - width / 2f;
        y = screenHeight / 2f - height / 2f;
    }

    public void moveWithCollision(float dx, float dy, GameMap map) {
        float newX = x + dx;
        float newY = y + dy;

        if (!map.isCellBlocked(newX, y)) {
            x = newX;
        }
        if (!map.isCellBlocked(x, newY)) {
            y = newY;
        }
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

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
