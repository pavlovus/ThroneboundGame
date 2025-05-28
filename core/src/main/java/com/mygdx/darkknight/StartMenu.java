package com.mygdx.darkknight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;


public class StartMenu implements Screen {
    private Stage stage;
    private Skin skin;
    private Texture backgroundTexture;
    private Music backgroundMusic;
    private Sound clickSound;

    public StartMenu() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("startingMenuImage.png"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Створюємо програмно базовий скин
        skin = new Skin();

        // Додаємо шрифт
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        // Створюємо білу текстуру 1x1 піксель для фону кнопки
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture pixmapTexture = new Texture(pixmap);
        skin.add("white", pixmapTexture);
        pixmap.dispose();

        // Створюємо стиль для кнопки
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.BLACK;

        skin.add("default", textButtonStyle);

        Texture textureUp = new Texture(Gdx.files.internal("startButtonImage.png"));
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(textureUp));
        style.font = font;
        style.fontColor = Color.WHITE;

        // Завантаження музики
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("startMusic.mp3"));
        backgroundMusic.setLooping(true); // повторювати без кінця

        // Завантаження звуку кліку
        clickSound = Gdx.audio.newSound(Gdx.files.internal("startButtonSound.mp3"));



        TextButton startButton = new TextButton("Start Game", style);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                ((Game) Gdx.app.getApplicationListener()).setScreen(new TbGame());
            }
        });

        TextButton exitButton = new TextButton("Exit", style);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                Gdx.app.exit();  // Закриває вікно та викликає dispose()
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.padTop(600);

        table.add(startButton).width(300).height(90).padBottom(20);
        table.row();
        table.add(exitButton).width(300).height(90);
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
        skin.dispose();
        backgroundMusic.dispose();
        clickSound.dispose();
    }

}
