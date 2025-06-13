package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.darkknight.effects.*;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.levels.FightLevel;
import com.mygdx.darkknight.levels.SecondLevel;
import com.mygdx.darkknight.levels.ThirdLevel;
import com.mygdx.darkknight.menus.PauseMenu;
import com.mygdx.darkknight.menus.RestartMenu;
import com.mygdx.darkknight.weapons.*;
import com.mygdx.darkknight.levels.FirstLevel;

import java.util.ArrayList;
import java.util.List;

public class TbGame implements Screen {
    private static final float BAR_WIDTH = 110;
    private static final float BAR_HEIGHT = 16;
    private static final float BAR_MARGIN = 22;
    private GameMap gameMap;
    private OrthographicCamera camera;
    private PauseMenu pauseMenu;
    private RestartMenu restartMenu;
    private boolean isPaused = false;
    private boolean gameOver = false;
    private SpriteBatch batch;
    private SpriteBatch uiBatch; // окремий шар для рендеру графічних ефектів, того, що лишатиметься нерухомим
    private ShapeRenderer shapeRenderer;
    private Hero hero;
    private Weapon weapon;
    private Texture bulletTexture;
    private Texture barBackgroundTexture;
    private Texture heartTexture;
    private Texture shieldTexture;
    private List<Bullet> bullets;
    private BitmapFont font;
    private GlyphLayout layout;
    private int width, height;
    private List<Enemy> enemies;

    private List<Enemy> enemiesToAdd = new ArrayList<>();

    private List<FightLevel> fightLevels = new ArrayList<>();
    private String currentLevelState = "INACTIVE";

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

        font = new BitmapFont();
        // TODO: Підібрати шрифт потім
        font.setColor(Color.WHITE);
        layout = new GlyphLayout();


        shapeRenderer = new ShapeRenderer();
        barBackgroundTexture = new Texture(Gdx.files.internal("barBackground.png"));
        heartTexture = new Texture(Gdx.files.internal("heart.png"));
        shieldTexture = new Texture(Gdx.files.internal("shield.png"));


        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        bulletTexture = new Texture("core/assets/arrow.png");

        //weapon = new SwordWeapon("core/assets/sword.png", 3, 32, 32, 32);
        weapon = new BowWeapon("core/assets/bow.png", 1, 20, 64, "core/assets/arrow.png");
        //weapon = new MagicWeapon("core/assets/magicWand.png", 3, 32, 32, "core/assets/fireball.png");
        //weapon = new AxeWeapon("core/assets/axe.png", 3, 32, 32, 32);
        hero = new Hero("core/assets/hero1.png",200, 120, 100, 5, weapon);
//        Swiftness testEffect = new Swiftness(10f, 500, new Texture(Gdx.files.internal("swiftness.png")));
//        hero.addEffect(testEffect);
//        Slowness testEffect1 = new Slowness(10f, 500, new Texture(Gdx.files.internal("slowness.png")));
//        hero.addEffect(testEffect1);
//        Weakness testEffect = new Weakness(100f, 1, new Texture(Gdx.files.internal("weakness.png")));
//        hero.addEffect(testEffect);
//        Power testEffect = new Power(100f, 2, new Texture(Gdx.files.internal("power.png")));
//        hero.addEffect(testEffect);
//        Poison testEffect = new Poison(20f, 1, 4f, new Texture(Gdx.files.internal("poison.png")));
//        hero.addEffect(testEffect);
//        Regeneration testEffect = new Regeneration(40f, 1, 4f, new Texture(Gdx.files.internal("regeneration.png")));
//        hero.addEffect(testEffect);


        fightLevels.add(new FirstLevel(3130, 70, 640, 380, gameMap, bullets, enemiesToAdd));
        fightLevels.add(new SecondLevel(3072, 1470, 1128, 576, gameMap, bullets));
        fightLevels.add(new ThirdLevel(2241, 2592, 1248, 701, gameMap, bullets));
    }

    @Override
    public void render(float delta) {

        // Перевірка на паузу під час гри (натискання ESC)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
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


        if (!isPaused && !gameOver) {
            // Обробка вводу, оновлення логіки лише коли гра не на паузі
            handleInput();
            hero.updateEffects(delta);

            enemiesToAdd.clear();

            // Оновлення ворогів
            for (Enemy e : enemies) e.update(hero, delta);

            enemies.addAll(enemiesToAdd);

            updateBullets(delta);
            removeDeadEnemies();
        }

        // Отримуємо позицію миші незалежно від паузи для правильного виведення зброї
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos); // Конвертуємо координати в систему камери

        float mouseX = mousePos.x;
        float mouseY = mousePos.y;

        weapon.updateAngle(mouseX, mouseY, hero.getCenterX(), hero.getCenterY());

        // Оновлюємо камеру, щоб слідувала за героєм
        float lerp = 3f * Gdx.graphics.getDeltaTime(); // коефіцієнт згладжування (можна 2–5)
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

        hero.draw(batch);
        for (Enemy e : enemies) e.draw(batch);
        weapon.update(delta, hero);
        if (hero.getCenterX() + weapon.getWidth() / 2f < mouseX)
            weapon.draw(batch, hero.getCenterX(), hero.getCenterY(), false);
        else
            weapon.draw(batch, hero.getCenterX(), hero.getCenterY(), true);

        for (Bullet b : bullets) {
            b.render(batch);
        }

        if (fightLevels.get(2).getStateName().equals("ACTIVE")) {
            fightLevels.get(2).drawMeteorStrikes(batch);
        }

        float barX = camera.position.x - (width / 2) + 20;
        float barY = camera.position.y + (height / 2) - 140;

        batch.end();

        // Рендеримо статичний інтерфейс
        renderUI();

        for (FightLevel level : fightLevels) {
            level.update(delta, hero, enemies);
            currentLevelState = level.getStateName();
            if (isPaused) {
                pauseMenu.render();
            }
            if (gameOver) {
                restartMenu.render();
            }
        }
    }

    private void renderUI() {
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
        font.getData().setScale(1.5f);
        font.draw(uiBatch, posText, 20, 30);
        font.draw(uiBatch, "Level: " + currentLevelState, 20, 55);
        uiBatch.end();
    }

    private void drawHeroBarText() {
        float barX = 80;
        float barY = height - 58;

        font.getData().setScale(2f);

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

            boolean bulletRemoved = false;

            if (!b.isOpponent()) {
                for (Enemy e : enemies) {
                    if (b.getBoundingRectangle().overlaps(e.getBoundingRectangle())) {

                        if (b instanceof MagicBullet magicBullet) {
                            if (!magicBullet.isExploded()) {
                                magicBullet.explode(enemies);
                            }
                        } else {
                            e.takeDamage(weapon.getDamage());
                            bullets.remove(i);
                            bulletRemoved = true;
                        }
                        break;
                    }
                }
            }

            if (!bulletRemoved && b.isOpponent() && b.getBoundingRectangle().overlaps(hero.getBoundingRectangle())) {
                hero.takeDamage(b.getEnemy().getDamage());
                bullets.remove(i);
                break;
            }
        }
    }

    private void handleInput() {
        if (isPaused) return;
        float delta = Gdx.graphics.getDeltaTime();
        float move = hero.getSpeed() * delta;
        boolean w = Gdx.input.isKeyPressed(Input.Keys.W);
        boolean a = Gdx.input.isKeyPressed(Input.Keys.A);
        boolean s = Gdx.input.isKeyPressed(Input.Keys.S);
        boolean d = Gdx.input.isKeyPressed(Input.Keys.D);

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
}
