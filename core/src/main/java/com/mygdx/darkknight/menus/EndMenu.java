package com.mygdx.darkknight.menus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class EndMenu implements Screen {
    private Stage stage;
    private Texture backgroundTexture;
    private Texture textureUp;
    private Texture textureOver;
    private Texture textureDown;
    private TextButton mainMenuButton;
    private BitmapFont font;
    private Music backgroundMusic;
    private Sound clickSound;
    private float alpha = 0;

    public EndMenu(String backgroundPath, String musicPath) {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        backgroundTexture = new Texture(Gdx.files.internal(backgroundPath));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Завантаження музики
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
        backgroundMusic.setLooping(true); // повторювати без кінця

        // Завантаження звуку кліку
        clickSound = Gdx.audio.newSound(Gdx.files.internal("startButtonSound.mp3"));

        font = new BitmapFont(Gdx.files.internal("medievalLightFont.fnt"));

        textureUp = new Texture(Gdx.files.internal("startButtonImage.png"));
        textureOver = new Texture(Gdx.files.internal("startButtonOver.png"));
        textureDown = new Texture(Gdx.files.internal("startButtonClicked.png"));
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(textureUp));
        style.over = new TextureRegionDrawable(new TextureRegion(textureOver));
        style.down = new TextureRegionDrawable(new TextureRegion(textureDown));
        style.font = font;
        style.fontColor = Color.valueOf("C0C0C0");

        mainMenuButton = new TextButton("Main menu", style);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                hide();
                Game game = (Game) Gdx.app.getApplicationListener();
                Screen oldScreen = game.getScreen();
                game.setScreen(new StartMenu());
                if (oldScreen != null) {
                    oldScreen.dispose();  // Звільняємо ресурси старого екрану
                }
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.padTop(screenHeight*85/100);

        table.add(mainMenuButton).width(screenWidth * 22/100).height(screenHeight * 12/100);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.getInputProcessor() != stage) {
            Gdx.input.setInputProcessor(stage);
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (alpha < 1f) {
            alpha += delta; // або alpha += delta * 0.5f; для повільнішого ефекту
            if (alpha > 1f) alpha = 1f;
        }

        stage.getBatch().setColor(1, 1, 1, alpha);
        stage.act(delta);
        stage.draw();
        stage.getBatch().setColor(1, 1, 1, 1); // reset

        // М’яке затемнення поверх
        Gdx.gl.glEnable(GL20.GL_BLEND);
        stage.getBatch().begin();
        stage.getBatch().setColor(0, 0, 0, 1f - alpha); // Чорне перекриття, що зникає
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override
    public void show() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        if (!backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
        Pixmap original = new Pixmap(Gdx.files.internal("cursorThronebound.png"));
        Pixmap scaled = new Pixmap(32, 32, original.getFormat()); // новий розмір

        // Масштабування вручну
        scaled.drawPixmap(original,
            0, 0, original.getWidth(), original.getHeight(),  // джерело
            0, 0, 32, 32                                       // призначення
        );

        Cursor cursor = Gdx.graphics.newCursor(scaled, 0, 0);
        Gdx.graphics.setCursor(cursor);

        // Обов'язково очистити ресурси
        original.dispose();
        scaled.dispose();
    }


    @Override public void resume() {}
    @Override public void pause() {}
    @Override public void hide() {
        backgroundMusic.stop();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        backgroundTexture.dispose();
        textureUp.dispose();
        font.dispose();
        backgroundMusic.dispose();
        clickSound.dispose();
        textureOver.dispose();
        textureDown.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
