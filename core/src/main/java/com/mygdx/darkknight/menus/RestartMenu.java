package com.mygdx.darkknight.menus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.mygdx.darkknight.TbGame;

public class RestartMenu {
    private TbGame game;
    private Stage stage;
    private Sound clickSound;
    private ShapeRenderer shapeRenderer;
    private Texture titleTexture;
    private boolean visible;
    private boolean disposed = false;

    public RestartMenu(TbGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        BitmapFont font = new BitmapFont(Gdx.files.internal("medievalLightFont.fnt"));

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.BLACK;

        Texture textureUp = new Texture(Gdx.files.internal("startButtonImage.png"));
        Texture textureOver = new Texture(Gdx.files.internal("startButtonOver.png"));
        Texture textureDown = new Texture(Gdx.files.internal("startButtonClicked.png"));
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(textureUp));
        style.over = new TextureRegionDrawable(new TextureRegion(textureOver));
        style.down = new TextureRegionDrawable(new TextureRegion(textureDown));
        style.font = font;
        style.fontColor = Color.valueOf("C0C0C0");

        shapeRenderer = new ShapeRenderer();

        titleTexture = new Texture(Gdx.files.internal("deathTitle.png"));

        Image titleImage = new Image(titleTexture);

        titleImage.setPosition(
            (Gdx.graphics.getWidth() - titleImage.getWidth()) / 2f,  // Центрування по ширині
            Gdx.graphics.getHeight() - titleImage.getHeight()   // Відступ від верху
        );

        stage.addActor(titleImage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.padTop(350);

        TextButton resumeButton = new TextButton("Restart", style);
        TextButton exitButton = new TextButton("Exit game", style);
        TextButton mainMenuButton = new TextButton("Main menu", style);

        table.add(resumeButton).width(300).height(90).pad(20);
        table.row();
        table.add(mainMenuButton).width(300).height(90).pad(20);
        table.row();
        table.add(exitButton).width(300).height(90).pad(20);

        stage.addActor(table);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("startButtonSound.mp3"));

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                hide();
                dispose();
                Gdx.input.setInputProcessor(null);

                Game gameApp = (Game) Gdx.app.getApplicationListener();
                Screen oldScreen = gameApp.getScreen();
                TbGame newGame = new TbGame();
                gameApp.setScreen(newGame);
                if (oldScreen != null) oldScreen.dispose();  // дуже важливо
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                Gdx.app.exit(); // Вийти з гри
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                hide();
                game.setPaused(false);
                Game game = (Game) Gdx.app.getApplicationListener();
                Screen oldScreen = game.getScreen();
                Gdx.input.setInputProcessor(null);
                game.setScreen(new StartMenu());
                if (oldScreen != null) oldScreen.dispose();
                dispose();
                if (oldScreen != null) {
                    oldScreen.dispose();  // Звільняємо ресурси старого екрану
                }
            }
        });
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
        visible = true;
    }

    public void hide() {
        Gdx.input.setInputProcessor(null); // повертає обробку вводу грі
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void render() {
        if (!visible) return;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.act();
        stage.draw();
    }

    public void dispose() {
        if (disposed) return;
        disposed = true;
        stage.dispose();
        shapeRenderer.dispose();
        clickSound.dispose();
        titleTexture.dispose();
    }
}
