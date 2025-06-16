package com.mygdx.darkknight.menus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.darkknight.TbGame;


public class StartMenu implements Screen {
    private Stage stage;
    private Texture backgroundTexture;
    private Texture titleTexture;
    private Texture subtitleTexture;
    private Texture textureUp;
    private Texture textureOver;
    private Texture textureDown;
    private TextButton startButton;
    private TextButton exitButton;
    private BitmapFont font;
    private Music backgroundMusic;
    private Sound clickSound;
    private float alpha = 0;

    public StartMenu() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("startingMenuImage.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        titleTexture = new Texture(Gdx.files.internal("title.png"));

        Image titleImage = new Image(titleTexture);

        titleImage.setPosition(
            (Gdx.graphics.getWidth() - titleImage.getWidth()) / 2f,  // Центрування по ширині
            Gdx.graphics.getHeight() - titleImage.getHeight()   // Відступ від верху
        );

        stage.addActor(titleImage);

        subtitleTexture = new Texture(Gdx.files.internal("subtitle.png"));

        Image subtitleImage = new Image(subtitleTexture);

        subtitleImage.setPosition(
            (Gdx.graphics.getWidth() - subtitleImage.getWidth()) / 2f,  // Центрування по ширині
            Gdx.graphics.getHeight() - subtitleImage.getHeight() - 150   // Відступ від верху
        );

        stage.addActor(subtitleImage);


        // Завантаження музики
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("startMusic.mp3"));
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

        // Кнопки Start Game і Exit тепер будуть використовувати цей стиль
        startButton = new TextButton("Start Game", style);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                Game game = (Game) Gdx.app.getApplicationListener();
                Gdx.input.setInputProcessor(null);
                game.setScreen(new LoadingScreen(game));
            }
        });

        exitButton = new TextButton("Exit", style);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                Gdx.app.exit();
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.padTop(500);

        table.row();
        table.add(startButton).width(300).height(90).padBottom(20);
        table.row();
        table.add(exitButton).width(300).height(90).padBottom(20);
        stage.addActor(table);

    }

    @Override
    public void render(float delta) {
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
        titleTexture.dispose();
        subtitleTexture.dispose();
        textureUp.dispose();
        font.dispose();
        backgroundMusic.dispose();
        clickSound.dispose();
        textureOver.dispose();
        textureDown.dispose();
        Gdx.input.setInputProcessor(null);
    }
}
