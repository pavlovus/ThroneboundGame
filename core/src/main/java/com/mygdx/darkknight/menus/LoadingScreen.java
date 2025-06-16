package com.mygdx.darkknight.menus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.darkknight.TbGame;

public class LoadingScreen implements Screen {
    private Texture backgroundTexture;
    private Game game;
    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;
    private float transitionAlpha = 0f;
    private boolean transitioning = false;
    private float progress;  // від 0 до 1
    private float alpha;     // для плавного появи (0-1)
    private boolean loadingComplete = false;
    private float dotTimer = 0f;
    private int dotCount = 0;

    public LoadingScreen(Game game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("medievalLightFontBigger.fnt"));
        shapeRenderer = new ShapeRenderer();

        progress = 0f;
        alpha = 0f;
    }

    @Override
    public void show() {
        backgroundTexture = new Texture(Gdx.files.internal("startingMenuImage.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {

        if (loadingComplete && !transitioning) {
            transitioning = true;
            transitionAlpha = 0f;
        }

        if (transitioning) {
            transitionAlpha += delta;
            if (transitionAlpha >= 1f) {
                game.setScreen(new TbGame());
                Gdx.input.setInputProcessor(null);
                dispose();
                return;
            }
        }

        if (alpha < 1f) {
            alpha += (1f - alpha) * 0.1f;
            if (alpha > 0.99f) alpha = 1f;
        }

        // Оновлення крапок у Loading...
        dotTimer += delta;
        if (dotTimer >= 0.5f) {
            dotTimer = 0f;
            dotCount = (dotCount + 1) % 4;
        }

        // Очищення екрану
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 1. Малюємо фон (не через stage, а напряму)
        batch.begin();
        batch.setColor(1, 1, 1, alpha);
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // 2. Імітація завантаження
        if (!loadingComplete) {
            progress += delta * 0.5f;
            if (progress >= 1f) {
                progress = 1f;
                loadingComplete = true;
            }
        }

        // 3. Прогрес-бар
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float barWidth = screenWidth * 0.6f;
        float barHeight = 12f;
        float x = (screenWidth - barWidth) / 2f;
        float y = screenHeight * 0.15f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, alpha);
        shapeRenderer.rect(x, y, barWidth, barHeight);
        shapeRenderer.setColor(0.7f, 0.7f, 0.7f, alpha);
        shapeRenderer.rect(x, y, barWidth * progress, barHeight);
        shapeRenderer.end();

        // 4. Текст Loading...
        batch.begin();
        font.setColor(1, 1, 1, alpha);
        String loadingText = "Loading" + ".".repeat(dotCount);
        float textX = (screenWidth - font.getScaleX() * loadingText.length() * 10) / 2f;
        font.draw(batch, loadingText, textX, y + 75);
        batch.end();

        // 5. Плавне затемнення при переході до гри
        if (transitioning) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, transitionAlpha);
            shapeRenderer.rect(0, 0, screenWidth, screenHeight);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
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
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
