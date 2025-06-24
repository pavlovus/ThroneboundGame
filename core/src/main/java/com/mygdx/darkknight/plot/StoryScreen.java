package com.mygdx.darkknight.plot;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.darkknight.TbGame;
import com.mygdx.darkknight.menus.EndMenu;
import com.mygdx.darkknight.menus.StartMenu;

public class StoryScreen {
    private TbGame game;
    private StoryManager storyManager;

    private Stage stage;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;

    private Texture panelTexture;
    private BitmapFont font;
    private BitmapFont ukrFont;
    private BitmapFont speakerFont;
    private GlyphLayout layout;
    private boolean end = true;
    private boolean visible;
    private boolean finished = false;

    private Table endTable;
    private Label endLabel;
    private TextButton option1;
    private TextButton option2;
    private Sound clickSound;

    public StoryScreen(TbGame game, StoryManager storyManager) {
        this(game, storyManager, false);
    }

    public StoryScreen(TbGame game, StoryManager storyManager, boolean end) {
        this.game = game;
        this.storyManager = storyManager;
        this.end = end;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        panelTexture = new Texture(Gdx.files.internal("assets/plotPanel.png"));
        font = new BitmapFont(Gdx.files.internal("medievalLightFontBigger.fnt"));
        font.setColor(Color.valueOf("#C0C0C0"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/Moyenage.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter smallParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParam.size = 36;
        smallParam.characters = "абвгґдеєжзиіїйклмнопрстуфхцчшщьюяАБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789,.!?-—:;()[]\"' ";
        smallParam.magFilter = Texture.TextureFilter.Nearest;
        smallParam.minFilter = Texture.TextureFilter.Nearest;

        ukrFont = generator.generateFont(smallParam);
        ukrFont.setColor(Color.valueOf("#C0C0C0"));
        generator.dispose();

        speakerFont = new BitmapFont(Gdx.files.internal("medievalLightFontBiggest.fnt"));
        speakerFont.setColor(Color.valueOf("#C0C0C0"));

        layout = new GlyphLayout();

        setupEndScreenUI();
    }

    private void setupEndScreenUI() {
        BitmapFont labelFont = new BitmapFont(Gdx.files.internal("medievalLightFontBiggest.fnt"));
        Label.LabelStyle labelStyle = new Label.LabelStyle(labelFont, Color.WHITE);

        endLabel = new Label("It's time to choose your destiny. Put on the crown or smash it to pieces.", labelStyle);

        BitmapFont buttonFont = new BitmapFont(Gdx.files.internal("medievalLightFontBigger.fnt"));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.WHITE;

        Texture textureUp = new Texture(Gdx.files.internal("startButtonImage.png"));
        Texture textureOver = new Texture(Gdx.files.internal("startButtonOver.png"));
        Texture textureDown = new Texture(Gdx.files.internal("startButtonClicked.png"));

        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(textureUp));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(textureOver));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(textureDown));

        option1 = new TextButton("Put on the Crown", buttonStyle);
        option1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                hide();
                Game game = (Game) Gdx.app.getApplicationListener();
                Screen oldScreen = game.getScreen();
                game.setScreen(new EndMenu("badEnding.png", "badEndingMusic.mp3"));
                if (oldScreen != null) {
                    oldScreen.dispose();  // Звільняємо ресурси старого екрану
                }
            }
        });
        option2 = new TextButton("Break the Crown", buttonStyle);
        option2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickSound.play();
                hide();
                Game game = (Game) Gdx.app.getApplicationListener();
                Screen oldScreen = game.getScreen();
                game.setScreen(new EndMenu("goodEnding.png", "goodEndingMusic.mp3"));
                if (oldScreen != null) {
                    oldScreen.dispose();  // Звільняємо ресурси старого екрану
                }
            }
        });

        endTable = new Table();
        endTable.setFillParent(true);
        endTable.center();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        endTable.add(endLabel).colspan(2).padBottom(screenHeight * 0.1f).row();
        endTable.add(option1).width(screenWidth * 33/100).height(screenHeight * 18/100).padRight(screenWidth * 0.05f);
        endTable.add(option2).width(screenWidth * 33/100).height(screenHeight * 18/100);
        endTable.setVisible(false);

        stage.addActor(endTable);

        clickSound = Gdx.audio.newSound(Gdx.files.internal("startButtonSound.mp3"));
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

        if (!finished && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (storyManager.hasNext()) {
                storyManager.nextScene();
            } else {
                if (!end) {
                    hide();
                    Gdx.input.setInputProcessor(null);
                    game.setPlotActive(false);
                } else {
                    finished = true;
                    endTable.setVisible(true);
                }
            }
        }

        // Затемнення
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if (!finished) {
            batch.begin();

            float screenWidth = Gdx.graphics.getWidth();
            float screenHeight = Gdx.graphics.getHeight();
            float panelHeight = screenHeight / 2f;

            batch.draw(panelTexture, 0, 0, screenWidth, panelHeight);

            Cutscene scene = storyManager.getCurrentScene();
            if (scene != null) {
                speakerFont.draw(batch, scene.getSpeaker(), screenWidth * 0.16f, panelHeight + 5);

                float textX = screenWidth * 0.12f;
                float textY = panelHeight - screenHeight * 0.14f;
                float textUkrY = panelHeight - screenHeight * 0.26f;
                float textWidth = screenWidth * 0.8f;

                layout.setText(font, scene.getText(), font.getColor(), textWidth, Align.left, true);
                font.draw(batch, layout, textX, textY);

                layout.setText(ukrFont, scene.getUkrText(), ukrFont.getColor(), textWidth, Align.left, true);
                ukrFont.draw(batch, layout, textX, textUkrY);
            }

            batch.end();
        }

        stage.act();
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
        batch.dispose();
        panelTexture.dispose();
        font.dispose();
        ukrFont.dispose();
        speakerFont.dispose();
        clickSound.dispose();
    }
}
