package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Hero {
    private Texture texture;
    private float x, y;
    private final int width = 38, height = 64;
    private int speed = 600;

    public Hero(String texturePath, int screenWidth, int screenHeight) {
        texture = new Texture(texturePath);
        x = screenWidth / 2f - width / 2f;
        y = screenHeight / 2f - height / 2f;
    }

    public void move(float dx, float dy) {
        float newX = x + dx;
        float newY = y + dy;

        if (newX >= 0 && newX <= Gdx.graphics.getWidth() - width)
            x = newX;
        if (newY >= 0 && newY <= Gdx.graphics.getHeight() - height)
            y = newY;
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
