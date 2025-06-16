package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.darkknight.bosses.FirstBossFightLevel;
import com.mygdx.darkknight.bosses.SecondBossFightLevel;
import com.mygdx.darkknight.bosses.ThirdBossFightLevel;
import com.mygdx.darkknight.effects.*;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.levels.*;
import com.mygdx.darkknight.menus.PauseMenu;
import com.mygdx.darkknight.menus.RestartMenu;
import com.mygdx.darkknight.plot.MultiStoryManager;
import com.mygdx.darkknight.plot.PlotCharacter;
import com.mygdx.darkknight.plot.StoryManager;
import com.mygdx.darkknight.plot.StoryScreen;
import com.mygdx.darkknight.weapons.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TbGame implements Screen {
    private static final float BAR_WIDTH = 110;
    private static final float BAR_HEIGHT = 16;
    private static final float BAR_MARGIN = 22;
    private final float EFFECT_ICON_WIDTH = 32f;
    private final float EFFECT_ICON_HEIGHT = 32f;
    private final float EFFECT_PADDING = 12f;
    private final float EFFECT_ANIM_DURATION = 0.3f;
    private float mouseX;
    private float mouseY;
    private GameMap gameMap;
    private OrthographicCamera camera;
    private PauseMenu pauseMenu;
    private RestartMenu restartMenu;
    private StoryScreen storyScreen;
    private boolean isPaused = false;
    private boolean gameOver = false;
    private boolean plotActive = false;
    private SpriteBatch batch;
    private SpriteBatch uiBatch; // окремий шар для рендеру графічних ефектів, того, що лишатиметься нерухомим
    private ShapeRenderer shapeRenderer;
    private Hero hero;
    private Weapon weapon;
    private Texture bulletTexture;
    private Texture barBackgroundTexture;
    private Texture heartTexture;
    private Texture shieldTexture;
    private Texture effectBackgroundTexture;
    private List<Bullet> bullets;
    private BitmapFont font;
    private BitmapFont smallFont;
    private final Map<Character, Float> effectAnimationTimers = new HashMap<>();
    private BitmapFont countFont;
    private GlyphLayout layout;
    private int width, height;
    private List<Enemy> enemies;
    private Texture defaultFrameTexture;
    private Texture selectedFrameTexture;
    private int selectedWeaponIndex = 0;
    private float animationTimer = 0f;
    private final float ANIMATION_DURATION = 0.2f;
    private ScreenFader fader = new ScreenFader();

    private List<Enemy> enemiesToAdd = new ArrayList<>();

    private List<FightLevel> fightLevels = new ArrayList<>();
    private List<Chest> chests = new ArrayList<>();
    private List<PlotCharacter> characters = new ArrayList<>();
    private Inventory inventory;
    private String currentLevelState = "INACTIVE";

    private List<Rectangle> weaponIconBounds = new ArrayList<>();

    @Override
    public void show() {
        // Завантажуємо всі текстури
        Assets.load();

        pauseMenu = new PauseMenu(this);
        restartMenu = new RestartMenu(this);
        gameMap = new GameMap("FirstMap.tmx");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);    //  Контролюємо стиснення камери

        batch = new SpriteBatch();
        uiBatch = new SpriteBatch();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        font = new BitmapFont(Gdx.files.internal("medievalLightFontSmaller.fnt"));
        font.setColor(Color.WHITE);
        countFont = new BitmapFont(Gdx.files.internal("medievalLightFontSmaller.fnt"));
        layout = new GlyphLayout();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("assets/pixelText.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter smallParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParam.size = 8;
        smallFont = generator.generateFont(smallParam);
        smallParam.magFilter = Texture.TextureFilter.Nearest;
        smallParam.minFilter = Texture.TextureFilter.Nearest;

        defaultFrameTexture = new Texture(Gdx.files.internal("assets/weaponBar.png"));
        selectedFrameTexture = new Texture(Gdx.files.internal("assets/weaponBarOver.png"));
        effectBackgroundTexture = new Texture("assets/spellsBar.png");

        shapeRenderer = new ShapeRenderer();
        barBackgroundTexture = new Texture(Gdx.files.internal("barBackground.png"));
        heartTexture = new Texture(Gdx.files.internal("heart.png"));
        shieldTexture = new Texture(Gdx.files.internal("shield.png"));


        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        bulletTexture = new Texture("core/assets/arrow.png");


        Weapon bow = new BowWeapon("core/assets/bowEpic.png", 1, 32, 64, "core/assets/arrowEpic.png");
        Weapon sword = new SwordWeapon("core/assets/swordEpic.png", 3, 32, 32);
        Weapon magic = new MagicWeapon("core/assets/magicWand.png", 3, 32, 32, "core/assets/fireball.png");
        Weapon wizard = new WizardWeapon("core/assets/magicStaff.png", 3, 32, 32, "core/assets/spark.png");
        Weapon axe = new AxeWeapon("core/assets/axeEpic.png", 3, 32, 32);
        Weapon mace = new MaceWeapon("core/assets/mace.png", 3, 32, 32, "core/assets/maceHit.png",32);
        hero = new Hero("core/assets/hero1.png",200, 120, 100, 10, weapon);


        fightLevels.add(new FirstLevel(3130, 70, 640, 380, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new SecondLevel(3072, 1470, 1128, 576, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new ThirdLevel(2241, 2592, 1248, 701, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new FirstBossFightLevel(3933, 3713, 904, 768, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new FourthLevel(3709, 5281, 969, 639, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new FifthLevel(322, 6174, 997, 419, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new SixthLevel(4798, 7550, 1288, 546, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new SeventhLevel(2367, 8896, 1481, 480, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new SecondBossFightLevel(5123, 8900, 994, 669, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new EighthLevel(2433, 9919, 965, 706, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new NinthLevel(3518, 11905, 841, 669, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new TenthLevel(3363, 13311, 1157, 510, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new ThirdBossFightLevel(3234, 16093, 1438, 674, gameMap, bullets, enemiesToAdd));

        Swiftness swiftness = new Swiftness(10f, 500, new Texture(Gdx.files.internal("swiftness.png")));
        Power power = new Power(100f, 2, new Texture(Gdx.files.internal("power.png")));
        Regeneration regeneration = new Regeneration(40f, 1, 4f, new Texture(Gdx.files.internal("regeneration.png")));

        // Видалити
        Chest chest1 = new Chest(11, 593, bow);
        Chest chest4 = new Chest(12, 593, swiftness);
        Chest chest5 = new Chest(13, 593, power);
        Chest chest6 = new Chest(14, 593, regeneration);
        Chest chest7 = new Chest(15, 593, swiftness);
        Chest chest8 = new Chest(16, 593, swiftness);
        Chest chest9 = new Chest(17, 593, swiftness);
        chests.add(chest1);
        chests.add(chest4);
        chests.add(chest5);
        chests.add(chest6);
        chests.add(chest7);
        chests.add(chest8);
        chests.add(chest9);
        // Видалити

        Chest chest11 = new Chest(151, 587, regeneration);
        Chest chest21 = new Chest(4, 511, axe);
        Chest chest31 = new Chest(6, 511, power);
        Chest chest41 = new Chest(123, 248, power);
        Chest chest51 = new Chest(166, 216, swiftness);
        Chest chest61 = new Chest(162, 216, regeneration);
        Chest chest71 = new Chest(52, 286, wizard);
        Chest chest81 = new Chest(72, 407, power);
        Chest chest91 = new Chest(95, 359, regeneration);
        Chest chest111 = new Chest(100, 359, swiftness);
        Chest chest121 = new Chest(17, 284, regeneration);
        Chest chest131 = new Chest(20, 284, swiftness);
        Chest chest141 = new Chest(164, 216, bow);
        Chest chest151 = new Chest(78, 444, regeneration);
        Chest chest161 = new Chest(157, 508, magic);
        Chest chest171 = new Chest(159, 508, swiftness);
        Chest chest181 = new Chest(161, 508, power);
        Chest chest191 = new Chest(127, 386, mace);
        Chest chest201 = new Chest(129, 386, regeneration);
        chests.add(chest11);
        chests.add(chest21);
        chests.add(chest31);
        chests.add(chest41);
        chests.add(chest51);
        chests.add(chest61);
        chests.add(chest71);
        chests.add(chest81);
        chests.add(chest91);
        chests.add(chest111);
        chests.add(chest121);
        chests.add(chest131);
        chests.add(chest141);
        chests.add(chest151);
        chests.add(chest161);
        chests.add(chest171);
        chests.add(chest181);
        chests.add(chest191);
        chests.add(chest201);
        inventory = new Inventory(gameMap);
        MultiStoryManager multiManager = new MultiStoryManager("core/assets/story.json");

        StoryManager intro = multiManager.getManager("intro");
        PlotCharacter introductionCharacter = new PlotCharacter(68, 594, intro, "core/assets/hero1.png");
        characters.add(introductionCharacter);
        PlotCharacter afterFirstFightCharacter = new PlotCharacter(160, 590, intro, "core/assets/hero1.png");
        characters.add(afterFirstFightCharacter);
        PlotCharacter elfCharacter = new PlotCharacter(189, 549, intro, "core/assets/hero1.png");
        characters.add(elfCharacter);
        PlotCharacter wizardCharacter = new PlotCharacter(192, 515, intro, "core/assets/hero1.png");
        characters.add(wizardCharacter);
        PlotCharacter knightCharacter = new PlotCharacter(77, 406, intro, "core/assets/hero1.png");
        characters.add(knightCharacter);
        PlotCharacter ghostCharacter = new PlotCharacter(132, 386, intro, "core/assets/hero1.png");
        characters.add(ghostCharacter);
        PlotCharacter priestCharacter = new PlotCharacter(44, 359, intro, "core/assets/hero1.png");
        characters.add(priestCharacter);
        PlotCharacter dwarfCharacter = new PlotCharacter(54, 286, intro, "core/assets/hero1.png");
        characters.add(dwarfCharacter);
        PlotCharacter fighterCharacter = new PlotCharacter(141, 285, intro, "core/assets/hero1.png");
        characters.add(fighterCharacter);
        PlotCharacter angelCharacter = new PlotCharacter(164, 179, intro, "core/assets/hero1.png");
        characters.add(angelCharacter);
        PlotCharacter finalCharacter = new PlotCharacter(125, 146, intro, "core/assets/hero1.png");
        characters.add(finalCharacter);
    }

    @Override
    public void render(float delta) {
        weapon = hero.getCurrentWeapon();
        // Перевірка на паузу під час гри (натискання ESC)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !gameOver && !plotActive) {
            isPaused = !isPaused;
            if (isPaused) {
                pauseMenu.show();
            } else {
                pauseMenu.hide();
                Gdx.input.setInputProcessor(null); // Повернути обробку вводу у TbGame
            }
        }

        if (hero.isDead() && !gameOver) {
            gameOver = true;
            restartMenu.show();
        }


        if (!isPaused && !gameOver && !plotActive) {
            // Обробка вводу, оновлення логіки лише коли гра не на паузі
            handleInput();
            Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(mousePos); // Конвертуємо координати в систему камери

            mouseX = mousePos.x;
            mouseY = mousePos.y;

            weapon.updateAngle(mouseX, mouseY, hero.getCenterX(), hero.getCenterY());
            hero.updateEffects(delta);

            enemiesToAdd.clear();

            // Оновлення ворогів
            new ArrayList<>(enemies).forEach(e -> e.update(hero, delta));

            enemies.addAll(enemiesToAdd);

            updateBullets(delta);
            removeDeadEnemies();
            handleWeaponNumberInput();
        }

        // Оновлюємо камеру, щоб слідувала за героєм
        float lerp = 2.3f * Gdx.graphics.getDeltaTime(); // коефіцієнт згладжування (можна 2–5)
        Vector3 position = camera.position;

        position.x += (hero.getCenterX() - position.x) * lerp;
        position.y += (hero.getCenterY() - position.y) * lerp;

        camera.position.set(position.x, position.y, 0);
        camera.update();

        // Очищаємо екран
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Рендеримо карту
        gameMap.render(camera);

        // Малюємо героя, ворогів, зброю і кулі
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Render chests
        for (Chest chest : chests) {
            if (chest.isVisible()) {
                chest.draw(batch);
            }
        }
        if(!gameOver){
            weapon.update(delta, hero);
            inventory.renderWeapons(batch);
            inventory.renderEffects(batch);
            if (hero.getCenterX() + weapon.getWidth() / 2f < mouseX) {
                hero.draw(batch, false);
                weapon.draw(batch, hero.getCenterX(), hero.getCenterY(), false);
            } else {
                hero.draw(batch, true);
                weapon.draw(batch, hero.getCenterX(), hero.getCenterY(), true);
            }
            for (Bullet b : bullets) {
                b.render(batch);
            }
        }
        for (Enemy e : enemies) e.draw(batch);

        for (FightLevel level : fightLevels) {
            if (level.getStateName().equals("ACTIVE")) {
                level.drawMeteorStrikes(batch);
                break; // Оскільки активний може бути тільки один рівень
            }
        }

        float barX = camera.position.x - (width / 2) + 20;
        float barY = camera.position.y + (height / 2) - 140;

        batch.end();

        // Рендеримо статичний інтерфейс
        renderUI(delta);

        // Update chests state
        uiBatch.begin();
        updateChests();
        updateCharacters();
        uiBatch.end();

        for (FightLevel level : fightLevels) {
            level.update(delta, hero, enemies);
            currentLevelState = level.getStateName();
        }
        if (isPaused) {
            pauseMenu.render();
        }
        if (gameOver) {
            restartMenu.render();
        }
        if (plotActive){
            storyScreen.render();
        }

        float x = hero.getX();
        float y = hero.getY();
        if (x < 4440 && 4340 < x && y < 4600 && 4539 < y) {
//            x = 2378;
//            y = 4921;
            if (fader != null) fader.startFadeOut(5f);
            hero.setLocation(2378, 4921);
            if (fader != null) fader.startFadeIn(5f);
        }

        if (x < 5988 && 5851 < x && y < 9690 && 9640 < y) {
//            x = 566;
//            y = 10015;
            if (fader != null) fader.startFadeOut(5f);
            hero.setLocation(566, 10015);
            if (fader != null) fader.startFadeIn(5f);
        }
        fader.update(delta);
        fader.render(shapeRenderer);
    }

    private void renderUI(float delta) {
        float barX = 20;
        float barY = height - 140;

        // Малюємо фон бару + іконки
        uiBatch.begin();
        uiBatch.draw(barBackgroundTexture, barX, barY, 200, 140);
        uiBatch.draw(heartTexture, barX + 25, barY + 74, 32, 32);
        uiBatch.draw(shieldTexture, barX + 25, barY + 34, 32, 32);
        uiBatch.end();
        // Малюємо бари здоров’я / броні через ShapeRenderer
        drawHeroBars();

        // Малюємо значення HP/Armor поверх барів
        uiBatch.begin();
        drawHeroBarText();
        uiBatch.end();

        // Координати героя
        drawCoordinates();
        renderHeroEffects();

        // Іконки зброї
        renderWeaponIcons(delta);
        renderEffectIcons(delta);
    }

    private void renderHeroEffects() {
        List<Effect> effects = hero.getActiveEffects();
        float startX = 230;
        float startY = height - 50;
        float size = 48;
        float padding = 8;

        uiBatch.begin();
        for (int i = 0; i < effects.size(); i++) {
            Effect effect = effects.get(i);
            Texture icon = effect.getIcon();
            if (icon != null) {
                float x = startX + i * (size + padding);
                float y = startY;
                uiBatch.draw(icon, x, y, size, size);
            }
        }
        uiBatch.end();
    }

    private void drawCoordinates() {
        uiBatch.begin();
        String posText = String.format("Hero: X=%.1f Y=%.1f", hero.getX(), hero.getY());
        font.draw(uiBatch, posText, 20, 30);
        font.draw(uiBatch, "Level: " + currentLevelState, 20, 55);
        uiBatch.end();
    }

    private void drawHeroBarText() {
        float barX = 80;
        float barY = height - 58;

        String hpText = hero.getHealth() + " / " + hero.getMaxHealth();
        layout.setText(font, hpText);
        float hpTextX = barX + (BAR_WIDTH - layout.width) / 2f;
        float hpTextY = barY + BAR_HEIGHT / 2f + layout.height / 2f;
        font.draw(uiBatch, layout, hpTextX, hpTextY);

        float armorY = barY - BAR_HEIGHT - BAR_MARGIN;
        String armorText = hero.getArmor() + " / " + hero.getMaxArmor();
        layout.setText(font, armorText);
        float armorTextX = barX + (BAR_WIDTH - layout.width) / 2f;
        float armorTextY = armorY + BAR_HEIGHT / 2f + layout.height / 2f;
        font.draw(uiBatch, layout, armorTextX, armorTextY);
    }

    private void drawHeroBars() {
        float barX = 80;
        float barY = height - 58;

        float healthPercentage = (float) hero.getHealth() / hero.getMaxHealth();
        float armorPercentage = (float) hero.getArmor() / hero.getMaxArmor();

        shapeRenderer.setProjectionMatrix(uiBatch.getProjectionMatrix()); // Використовуємо screen projection
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Здоров'я
        shapeRenderer.setColor(0.1f, 0.02f, 0.02f, 1);
        shapeRenderer.rect(barX, barY, BAR_WIDTH, BAR_HEIGHT);
        shapeRenderer.setColor(0.4f, 0.05f, 0.05f, 1);
        shapeRenderer.rect(barX, barY, BAR_WIDTH * healthPercentage, BAR_HEIGHT);

        // Броня
        float armorY = barY - BAR_HEIGHT - BAR_MARGIN;
        shapeRenderer.setColor(0.05f, 0.05f, 0.1f, 1);
        shapeRenderer.rect(barX, armorY, BAR_WIDTH, BAR_HEIGHT);
        shapeRenderer.setColor(0.15f, 0.25f, 0.4f, 1);
        shapeRenderer.rect(barX, armorY, BAR_WIDTH * armorPercentage, BAR_HEIGHT);

        shapeRenderer.end();
    }

    private void renderWeaponIcons(float delta) {
        List<Weapon> weapons = hero.getWeapons();

        float iconSize = 32f;
        float frameWidth = iconSize + 30;
        float frameHeight = iconSize + 65;
        float padding = 12f;
        int count = weapons.size();
        float totalWidth = count * frameWidth + (count - 1) * padding;

        float startX = width - totalWidth - 20;
        float startY = height - frameHeight - 20;

        // Оновлюємо таймер
        if (animationTimer > 0f) {
            animationTimer -= delta;
            if (animationTimer < 0f) animationTimer = 0f;
        }

        weaponIconBounds.clear();
        uiBatch.begin();
        for (int i = 0; i < count; i++) {
            Weapon w = weapons.get(i);
            float x = startX + i * (frameWidth + padding);
            float y = startY;

            boolean isSelected = (i == selectedWeaponIndex);
            Texture frameTexture = isSelected ? selectedFrameTexture : defaultFrameTexture;

            // Анімація лише для вибраного
            float scale = 1f;
            if (isSelected && animationTimer > 0f) {
                float progress = 1f - (animationTimer / ANIMATION_DURATION);
                scale = 1f + 0.25f * (float) Math.sin(progress * Math.PI); // пульс
            }

            float scaledWidth = frameWidth * scale;
            float scaledHeight = frameHeight * scale;
            float offsetX = (scaledWidth - frameWidth) / 2f;
            float offsetY = (scaledHeight - frameHeight) / 2f;

            // Малюємо рамку
            uiBatch.draw(frameTexture, x - offsetX, y - offsetY - 5, scaledWidth, scaledHeight);

            // Іконка — всередині рамки
            float iconX = x + (frameWidth - iconSize) / 2f;
            float iconY = y + (frameHeight - iconSize) / 2f - 5f;
            uiBatch.draw(w.getTexture(), iconX, iconY, iconSize, iconSize);

            // Цифра
            font.getData().setScale(1f);
            font.setColor(Color.WHITE);
            font.draw(uiBatch, String.valueOf(i + 1), x + 16, y + frameHeight - 40);

            weaponIconBounds.add(new Rectangle(x, y, frameWidth, frameHeight));
        }
        uiBatch.end();
    }

    private void renderEffectIcons(float delta) {
        List<Effect> allEffects = hero.getSpells();
        Map<Character, List<Effect>> categorized = new HashMap<>();
        categorized.put('F', new ArrayList<>());
        categorized.put('G', new ArrayList<>());
        categorized.put('H', new ArrayList<>());

        for (Effect e : allEffects) {
            char key = categorizeEffect(e);
            if (categorized.containsKey(key)) {
                categorized.get(key).add(e);
            }
        }

        float startX = 20f;
        float barY = height - 58 - 2 * BAR_HEIGHT - BAR_MARGIN;
        float startY = barY - 40f - EFFECT_ICON_HEIGHT;

        uiBatch.begin();
        int index = 0;
        for (char key : new char[]{'F', 'G', 'H'}) {
            List<Effect> effects = categorized.get(key);
            if (effects.isEmpty()) {
                index++;
                continue;
            }

            float x = startX + index * (EFFECT_ICON_WIDTH + EFFECT_PADDING + 10f) + 25f;
            float y = startY;

            // Анімація
            float scale = 1f;
            if (effectAnimationTimers.containsKey(key)) {
                float timer = effectAnimationTimers.get(key);
                timer -= delta;
                if (timer <= 0f) {
                    timer = 0f;
                    effectAnimationTimers.remove(key);
                } else {
                    scale = 1f + 0.25f * (float) Math.sin((1 - timer / EFFECT_ANIM_DURATION) * Math.PI);
                    effectAnimationTimers.put(key, timer);
                }
            }

            float scaledWidth = EFFECT_ICON_WIDTH * scale;
            float scaledHeight = EFFECT_ICON_HEIGHT * scale;
            float offsetX = (scaledWidth - EFFECT_ICON_WIDTH) / 2f;
            float offsetY = (scaledHeight - EFFECT_ICON_HEIGHT) / 2f;

            // Фон
            if (effectBackgroundTexture != null)
                uiBatch.draw(effectBackgroundTexture, x - offsetX - 10f, y - offsetY - 15f, scaledWidth * 1.8f, scaledHeight * 1.8f);

            // Іконка ефекту
            Texture iconTexture = effects.get(0).getTexture();
            uiBatch.draw(iconTexture, x - offsetX, y - offsetY, scaledWidth, scaledHeight);

            // Літера клавіші
            font.setColor(Color.WHITE);
            font.getData().setScale(1f);
            font.draw(uiBatch, String.valueOf(key), x + 10f, y - 10f);

            // Кількість ефектів
            countFont.setColor(Color.YELLOW);
            countFont.getData().setScale(1f);
            countFont.draw(uiBatch, String.valueOf(effects.size()), x + EFFECT_ICON_WIDTH - 6f, y + 14f);

            index++;
        }
        uiBatch.end();

        // Обробка натискання
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            activateEffect(categorized.get('F'), hero);
            effectAnimationTimers.put('F', EFFECT_ANIM_DURATION);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
            activateEffect(categorized.get('G'), hero);
            effectAnimationTimers.put('G', EFFECT_ANIM_DURATION);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            activateEffect(categorized.get('H'), hero);
            effectAnimationTimers.put('H', EFFECT_ANIM_DURATION);
        }
    }

    private char categorizeEffect(Effect effect) {
        String name = effect.getName().toLowerCase();
        if (name.contains("regeneration")) return 'F';
        if (name.contains("power")) return 'G';
        return 'H';
    }

    private void activateEffect(List<Effect> effects, Hero hero) {
        if (effects != null && !effects.isEmpty()) {
            Effect toApply = effects.get(0);
            hero.addEffect(toApply);     // Додаємо у список активних
            hero.consumeEffect(toApply); // Видаляємо з запасу
        }
    }

    public void updateChests() {
        inventory.showChest(chests);

        boolean justOpenedChest = false;

        for (Chest chest : chests) {
            if (!chest.isOpened() && inventory.isPlayerNearChest(hero, chest)) {
                float textX = chest.getX() * 32 + chest.getWidth() / 2f + 9;
                float textY = chest.getY() * 32 + chest.getHeight() + 10 - 32;
                String label = "Enter to open";
                layout.setText(smallFont, label);
                batch.begin();
                smallFont.draw(batch, layout, textX - layout.width / 2f, textY);
                batch.end();

                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    inventory.openChest(chest);
                    justOpenedChest = true;
                    break;
                }
            }
        }

        if (!justOpenedChest) {
            for (Chest chest : chests) {
                if (chest.isOpened() && chest.getWeapon() != null && inventory.isPlayerNearContent(hero, chest)) {
                    String message = chest.getWeapon().getName();
                    layout.setText(smallFont, message);
                    float textWidth = layout.width;
                    float centeredX = chest.getX() * 32 + 32 / 2f - textWidth / 2f;
                    batch.begin();
                    smallFont.draw(batch, layout, centeredX, chest.getY() * 32 - 96 + 9 + 20);
                    batch.end();

                    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                        hero.addWeapon(chest.getWeapon());
                        chest.setWeapon(null);
                        break;
                    }
                }

                if (chest.isOpened() && chest.getEffect() != null && inventory.isPlayerNearContent(hero, chest)) {
                    String message = chest.getEffect().getName();
                    layout.setText(smallFont, message);
                    float textWidth = layout.width;
                    float centeredX = chest.getX() * 32 + 32 / 2f - textWidth / 2f;
                    batch.begin();
                    smallFont.draw(batch, layout, centeredX, chest.getY() * 32 - 96 + 9 + 20);
                    batch.end();

                    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                        hero.addSpell(chest.getEffect());
                        chest.setEffect(null);
                        break;
                    }
                }
            }
        }
    }

    private void updateCharacters() {
        for (PlotCharacter character : characters) {
            batch.begin();
            batch.draw(character.getTexture(), character.getX() * 32, (character.getY() - 1) * 32, 0, 0);
            batch.end();
        }

        for (PlotCharacter character : characters) {
            if (!character.isTalked() && character.isPlayerNearCharacter(hero)) {
                float textX = character.getX() * 32 + 32/ 2f;
                float textY = character.getY() * 32 + 15;
                String label = "Enter to talk";
                layout.setText(smallFont, label);
                batch.begin();
                smallFont.draw(batch, layout, textX - layout.width / 2f, textY);
                batch.end();
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    plotActive = !plotActive;
                    if (plotActive) {
                        storyScreen = new StoryScreen(this, character.getScene());
                        storyScreen.show();
                    }
                    character.setTalked(true);
                    break;
                }
            }
        }
    }

    private void handleWeaponNumberInput() {
        List<Weapon> weapons = hero.getWeapons();
        for (int i = 0; i < weapons.size(); i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                if (selectedWeaponIndex != i) {
                    selectedWeaponIndex = i;
                    hero.setCurrentWeapon(weapons.get(i));
                    animationTimer = ANIMATION_DURATION; // запускаємо анімацію
                }
                break;
            }
        }
    }

    private void removeDeadEnemies() {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            if (enemies.get(i).isDead()) {
                enemies.remove(i);
            }
        }
    }

    private void updateBullets(float delta) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update(delta, gameMap, enemies);

            if (b.shouldRemove()) {
                bullets.remove(i);
                continue;
            }

            if (!b.isOpponent()) {
                for (Enemy e : enemies) {
                    if (b.getBoundingRectangle().overlaps(e.getBoundingRectangle())) {

                        if (b instanceof MagicBullet magicBullet) {
                            if (!magicBullet.isExploded()) {
                                magicBullet.explode(enemies);
                            }
                        } else {
                            if(!b.isStrike()){
                                b.strike(e);
                            }
                        }
                        break;
                    }
                }
            }

            if (b.isOpponent() && b.getBoundingRectangle().overlaps(hero.getBoundingRectangle())) {
                if(!b.isStrike()){
                    b.strike(b.getEnemy(), hero);
                }
                break;
            }
        }
    }

    private void handleInput() {
        if (isPaused || plotActive) return;
        float delta = Gdx.graphics.getDeltaTime();
        float move = hero.getSpeed() * delta;
        boolean w = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.DPAD_UP);
        boolean a = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT);
        boolean s = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN);
        boolean d = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT);

        float dx = 0, dy = 0;
        if (w) dy += move;
        if (s) dy -= move;
        if (a) dx -= move;
        if (d) dx += move;

        if (dx != 0 && dy != 0) {
            dx /= (float) Math.sqrt(2);
            dy /= (float) Math.sqrt(2);
        }

        hero.moveWithCollision(dx, dy, gameMap); // <<< ДОБАВЛЕНО - метод з перевіркою колізій

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            weapon.attack(hero, bullets, enemies);
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        uiBatch.dispose();
        shapeRenderer.dispose();
        gameMap.dispose();
        font.dispose();
        hero.dispose();
        weapon.dispose();
        bulletTexture.dispose();
        barBackgroundTexture.dispose();
        heartTexture.dispose();
        shieldTexture.dispose();
        pauseMenu.dispose();
        restartMenu.dispose();
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setPlotActive(boolean plotActive) {
        this.plotActive = plotActive;
    }
}
