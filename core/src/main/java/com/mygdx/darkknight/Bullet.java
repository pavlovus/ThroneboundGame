package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    private static final float SPEED = 800f;
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;

    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private Texture texture;

    public Bullet(float startX, float startY, float angleDegrees, Texture texture) {
        this.texture = texture;
        this.position = new Vector2(startX - WIDTH / 2f, startY - HEIGHT / 2f);
        this.angle = angleDegrees;
        float angleRadians = (float) Math.toRadians(angleDegrees);
        this.velocity = new Vector2((float) Math.cos(angleRadians), (float) Math.sin(angleRadians)).scl(SPEED);
    }

    public void update(float delta) {
        position.mulAdd(velocity, delta);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, WIDTH / 2f, HEIGHT / 2f, WIDTH, HEIGHT, 1, 1, angle, 0, 0, texture.getWidth(), texture.getHeight(), false, false);

    }

    public boolean isOffScreen(int screenWidth, int screenHeight) {
        return position.x < -WIDTH || position.x > screenWidth || position.y < -HEIGHT || position.y > screenHeight;
    }
}
