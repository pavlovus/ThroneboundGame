package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class PauseMenu {
    private TbGame game;
    private Stage stage;
    private Sound clickSound;
    private ShapeRenderer shapeRenderer;
    private boolean visible;

    public PauseMenu(TbGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("GothicPixels.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.BLACK;

        Texture textureUp = new Texture(Gdx.files.internal("startButtonImage.png"));
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new TextureRegionDrawable(new TextureRegion(textureUp));
        style.font = font;
        style.fontColor = Color.WHITE;

        shapeRenderer = new ShapeRenderer();

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        TextButton resumeButton = new TextButton("Resume", style);
        TextButton inventoryButton = new TextButton("Inventory", style);
        TextButton exitButton = new TextButton("Exit game", style);
        TextButton mainMenuButton = new TextButton("Main menu", style);

        table.add(resumeButton).width(200).height(50).pad(10);
        table.row();
        table.add(inventoryButton).width(200).height(50).pad(10);
        table.row();
        table.add(exitButton).width(200).height(50).pad(10);
        table.row();
        table.add(mainMenuButton).width(200).height(50).pad(10);

        stage.addActor(table);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("startButtonSound.mp3"));

        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                hide();
                game.setPaused(false);
            }
        });

        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                System.out.println("Відкриваємо інвентар (реалізуй сам)");
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
                System.out.println("Main menu: логіка ще не реалізована");
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
        stage.dispose();
        shapeRenderer.dispose();
        clickSound.dispose();
    }
}
