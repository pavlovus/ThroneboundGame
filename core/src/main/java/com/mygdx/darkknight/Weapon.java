package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Weapon {
    private Texture texture;
    private final int width = 20, height = 64;

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    private float angle = 0;

    public Weapon(String texturePath) {
        texture = new Texture(texturePath);
    }

    public void draw(SpriteBatch batch, float centerX, float centerY) {
        batch.draw(
            texture,
            centerX, centerY - height/2,
            width / 2f, height / 2f,
            width, height,
            1, 1,
            angle,
            0, 0,
            texture.getWidth(), texture.getHeight(),
            false, false
        );
    }

    public void updateAngle(float mouseX, float mouseY, float heroX, float heroY) {
        float dx = mouseX - (heroX + width / 2f);
        float dy = mouseY - (heroY - height / 2f);
        angle = (float) Math.toDegrees(Math.atan2(dy, dx));
    }

    public void dispose() {
        texture.dispose();
    }
}
