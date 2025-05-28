package com.mygdx.darkknight;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

public class TbGame extends ApplicationAdapter {
    SpriteBatch batch;
    Hero hero;
    Weapon weapon;

    List<Bullet> bullets;
    Texture bulletTexture;


    int width, height;

    @Override
    public void create() {
        bullets = new ArrayList<>();
        bulletTexture = new Texture("core/assets/bullet.png");

        batch = new SpriteBatch();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        hero = new Hero("core/assets/hero.png", width, height);
        weapon = new Weapon("core/assets/gun.png");
    }

    @Override
    public void render() {
        handleInput();

        float mouseX = Gdx.input.getX();
        float mouseY = height - Gdx.input.getY();

        weapon.updateAngle(mouseX, mouseY, hero.getCenterX(), hero.getCenterY());

        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        hero.draw(batch);
        weapon.draw(batch, hero.getCenterX(), hero.getCenterY());
        batch.end();

        float delta = Gdx.graphics.getDeltaTime();
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(delta);
            if (b.isOffScreen(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) {
                bullets.remove(i);
            }
        }

        batch.begin();
        for (Bullet b : bullets) {
            b.render(batch);
        }
        batch.end();

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
            dx /= (float) Math.sqrt(2);
            dy /= (float) Math.sqrt(2);
        }

        hero.move(dx, dy);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float weaponAngle = weapon.getAngle(); // потрібен геттер

            float gunX = hero.getCenterX();
            float gunY = hero.getCenterY();

            bullets.add(new Bullet(gunX, gunY, weaponAngle, bulletTexture));
        }


    }

    @Override
    public void dispose() {
        hero.dispose();
        weapon.dispose();
        batch.dispose();
    }
}
