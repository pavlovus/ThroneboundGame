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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StartMenu implements Screen {
    private Stage stage;
    private Skin skin;

    public StartMenu() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

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
        textButtonStyle.up = new TextureRegionDrawable(new TextureRegion(pixmapTexture));
        textButtonStyle.down = new TextureRegionDrawable(new TextureRegion(pixmapTexture));
        textButtonStyle.checked = new TextureRegionDrawable(new TextureRegion(pixmapTexture));
        textButtonStyle.over = new TextureRegionDrawable(new TextureRegion(pixmapTexture));

//        // Різні кольори для різних станів кнопки
//        textButtonStyle.up.setTint(Color.LIGHT_GRAY);
//        textButtonStyle.down.setTint(Color.DARK_GRAY);
//        textButtonStyle.checked.setTint(Color.DARK_GRAY);
//        textButtonStyle.over.setTint(Color.GRAY);

        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.BLACK;

        skin.add("default", textButtonStyle);

        // Створюємо кнопку
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(new TbGame());
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(startButton).width(200).height(60);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void resume() {}
    @Override public void pause() {}
    @Override public void hide() {}
    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
