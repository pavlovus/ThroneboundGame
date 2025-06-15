package com.mygdx.darkknight.plot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.darkknight.TbGame;

public class StoryScreen {
    private TbGame game;
    private StoryManager storyManager;

    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;

    private Texture panelTexture;
    private BitmapFont font;
    private BitmapFont speakerFont;
    private GlyphLayout layout;

    private boolean visible;

    public StoryScreen(TbGame game, StoryManager storyManager) {
        this.game = game;
        this.storyManager = storyManager;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        panelTexture = new Texture(Gdx.files.internal("assets/plotPanel.png"));
        font = new BitmapFont(Gdx.files.internal("medievalLightFont.fnt"));
        font.setColor(Color.valueOf("#C0C0C0"));
        speakerFont = new BitmapFont(Gdx.files.internal("medievalLightFontBigger.fnt"));
        speakerFont.setColor(Color.valueOf("#C0C0C0"));
        layout = new GlyphLayout();
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
        visible = true;
    }

    public void hide() {
        Gdx.input.setInputProcessor(null);
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void render() {
        if (!visible) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if(storyManager.hasNext()){
                storyManager.nextScene();
            } else {
                hide();
                Gdx.input.setInputProcessor(null);
                game.setPlotActive(false);
            }
        }

        // Затемнення
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Вивід панелі та тексту
        batch.begin();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float panelHeight = screenHeight / 2f;

        // Малюємо панель
        batch.draw(panelTexture, 0, 0, screenWidth, panelHeight);

        // Отримуємо поточну сцену
        Cutscene scene = storyManager.getCurrentScene();
        if (scene != null) {
            // Ім’я персонажа (вгорі зліва)
            speakerFont.draw(batch, scene.getSpeaker(), 195, panelHeight);

            // Основний текст (у панелі, з відступами)
            float textX = 160;
            float textY = panelHeight - 110;
            float textWidth = screenWidth - 60;

            layout.setText(font, scene.getText(), font.getColor(), textWidth, Align.left, true);
            font.draw(batch, layout, textX, textY);
        }

        batch.end();

        stage.act();
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
        batch.dispose();
        panelTexture.dispose();
        font.dispose();
    }
}
