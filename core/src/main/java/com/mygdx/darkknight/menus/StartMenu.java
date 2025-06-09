package com.mygdx.darkknight.menus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
    private TextButton startButton;
    private TextButton exitButton;
    private BitmapFont font;
    private Music backgroundMusic;
    private Sound clickSound;

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
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(textureUp));
        style.font = font;
        style.fontColor = Color.valueOf("C0C0C0");

        // Кнопки Start Game і Exit тепер будуть використовувати цей стиль
        startButton = new TextButton("Start Game", style);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                Game game = (Game) Gdx.app.getApplicationListener();
                Screen oldScreen = game.getScreen();
                game.setScreen(new TbGame());
                if (oldScreen != null) {
                    oldScreen.dispose();  // Звільняємо ресурси старого екрану
                }
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
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
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
    }


    @Override public void resume() {}
    @Override public void pause() {}
    @Override public void hide() {
        backgroundMusic.stop();
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
        Gdx.input.setInputProcessor(null);
    }
}
