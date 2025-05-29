package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.ArrayList;
import java.util.List;

public class TbGame implements Screen {
    private SpriteBatch batch;
    private Hero hero;
    private Weapon weapon;
    private Texture bulletTexture;
    private List<Bullet> bullets;

    private int width, height;

    @Override
    public void show() {
        batch = new SpriteBatch();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        hero = new Hero("core/assets/hero.png", width, height);
        weapon = new Weapon("core/assets/gun.png");
        bulletTexture = new Texture("core/assets/bullet.png");
        bullets = new ArrayList<>();
    }

    @Override
    public void render(float delta) {
        handleInput();

        float mouseX = Gdx.input.getX();
        float mouseY = height - Gdx.input.getY();

        weapon.updateAngle(mouseX, mouseY, hero.getCenterX(), hero.getCenterY());

        // Clear screen
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw hero and weapon
        batch.begin();
        hero.draw(batch);
        weapon.draw(batch, hero.getCenterX(), hero.getCenterY());
        for (Bullet b : bullets) {
            b.render(batch);
        }
        batch.end();

        // Update bullets
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(delta);
            if (b.isOffScreen(width, height)) {
                bullets.remove(i);
            }
        }
    }

    private void handleInput() {
        float delta = Gdx.graphics.getDeltaTime();
        float move = hero.getSpeed() * delta;
        boolean w = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean a = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean s = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean d = Gdx.input.isKeyPressed(Input.Keys.D);

        float dx = 0, dy = 0;
        if (w) dy += move;
        if (s) dy -= move;
        if (a) dx -= move;
        if (d) dx += move;

        if (dx != 0 && dy != 0) {
            dx /= Math.sqrt(2);
            dy /= Math.sqrt(2);
        }

        hero.move(dx, dy);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float weaponAngle = weapon.getAngle();
            float gunX = hero.getCenterX();
            float gunY = hero.getCenterY();
            bullets.add(new Bullet(gunX, gunY, weaponAngle, bulletTexture));
        }
    }

    @Override public void resize(int width, int height) { }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        hero.dispose();
        weapon.dispose();
        bulletTexture.dispose();
    }
}
