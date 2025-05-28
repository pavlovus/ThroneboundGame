package com.mygdx.darkknight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Input.Keys;

public class TbGame implements Screen {
    private ShapeRenderer shapeRenderer;
    private float x, y;

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        x = 100;
        y = 100;
    }

    @Override
    public void render(float delta) {
        handleInput(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1);
        shapeRenderer.rect(x, y, 32, 32);
        shapeRenderer.end();
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyPressed(Keys.LEFT)) x -= 200 * delta;
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) x += 200 * delta;
        if (Gdx.input.isKeyPressed(Keys.UP)) y += 200 * delta;
        if (Gdx.input.isKeyPressed(Keys.DOWN)) y -= 200 * delta;
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}
