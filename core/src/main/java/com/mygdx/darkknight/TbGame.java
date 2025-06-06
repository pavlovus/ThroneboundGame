package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class TbGame implements Screen {
    private static final float BAR_WIDTH = 110;
    private static final float BAR_HEIGHT = 16;
    private static final float BAR_MARGIN = 22;
    private GameMap gameMap;
    private OrthographicCamera camera;
    private PauseMenu pauseMenu;
    private boolean isPaused = false;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Hero hero;
    private Weapon weapon;
    private Texture bulletTexture;
    private Texture enemyTexture;
    private Texture barBackgroundTexture;
    private Texture heartTexture;
    private Texture shieldTexture;
    private List<Bullet> bullets;
    private BitmapFont font;
    private GlyphLayout layout;

    private int width, height;
    private List<Enemy> enemies;
    private List<FightLevel> fightLevels = new ArrayList<>();

    @Override
    public void show() {
        pauseMenu = new PauseMenu(this);
        System.out.println("🔍 show() запущено");
        gameMap = new GameMap("FirstMap.tmx");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();
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
        enemyTexture = new Texture("core/assets/skeleton.png");

        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        bulletTexture = new Texture("core/assets/arrow.png");
        bullets = new ArrayList<>();

        hero = new Hero("core/assets/hero1.png", 200, 120, 100, 5);
        weapon = new Weapon("core/assets/bow.png", 1);

        fightLevels.add(new FightLevel(
                3120, 70, 650, 380, 5, bulletTexture, bullets, gameMap
        ));
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

        if (!isPaused) {
            // Обробка вводу, оновлення логіки лише коли гра не на паузі
            handleInput();

            // Оновлення ворогів
            for (Enemy e : enemies) e.update(hero, delta);
        }

        // Отримуємо позицію миші незалежно від паузи для правильного виведення зброї
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos); // Конвертуємо координати в систему камери

        float mouseX = mousePos.x;
        float mouseY = mousePos.y;

        weapon.updateAngle(mouseX, mouseY, hero.getCenterX(), hero.getCenterY());

        // Оновлюємо камеру, щоб слідувала за героєм
        camera.position.set(hero.getCenterX(), hero.getCenterY(), 0);
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
        if (hero.getCenterX() + weapon.getWidth() / 2f < mouseX)
            weapon.draw(batch, hero.getCenterX(), hero.getCenterY(), false);
        else
            weapon.draw(batch, hero.getCenterX(), hero.getCenterY(), true);

        for (Bullet b : bullets) {
            b.render(batch);
        }

        float barX = camera.position.x - (width / 2) + 20;
        float barY = camera.position.y + (height / 2) - 140;

        batch.draw(barBackgroundTexture, barX, barY, 200, 140);
        batch.draw(heartTexture, barX + 25, barY + 74, 32, 32);
        batch.draw(shieldTexture, barX + 25, barY + 34, 32, 32);
        batch.end();

        drawHeroBars();
        //Текст поверх барів
        batch.begin();
        drawHeroBarText();
        batch.end();

        updateBullets(delta);
        removeDeadEnemies();

        drawCoordinateSystem();

        for (FightLevel level : fightLevels) {
            level.activateIfNeeded(hero, enemies);
        }

        if (isPaused) pauseMenu.render();
    }

    private void drawCoordinateSystem() {
        Vector3 mouseWorld = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
        font.getData().setScale(2f);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.draw(batch, String.format("Hero: X=%.1f Y=%.1f", hero.getX(), hero.getY()), camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y + camera.viewportHeight / 2 - 120);
        font.draw(batch, String.format("Mouse: X=%.1f Y=%.1f", mouseWorld.x, mouseWorld.y), camera.position.x - camera.viewportWidth / 2 + 10, camera.position.y + camera.viewportHeight / 2 - 160);
        batch.end();
    }


    private void drawHeroBarText() {
        float barX = Math.round(camera.position.x - (width / 2f) + 80);
        float barY = Math.round(camera.position.y + (height / 2f) - 58);

        String hpText = hero.getHealth() + " / " + hero.getMaxHealth();
        layout.setText(font, hpText);
        float hpTextX = Math.round(barX + (BAR_WIDTH - layout.width) / 2f);
        float hpTextY = Math.round(barY + BAR_HEIGHT / 2f + layout.height / 2f);
        font.draw(batch, layout, hpTextX, hpTextY);

        float armorY = Math.round(barY - BAR_HEIGHT - BAR_MARGIN);
        String armorText = hero.getArmor() + " / " + hero.getMaxArmor();
        layout.setText(font, armorText);
        float armorTextX = Math.round(barX + (BAR_WIDTH - layout.width) / 2f);
        float armorTextY = Math.round(armorY + BAR_HEIGHT / 2f + layout.height / 2f);
        font.draw(batch, layout, armorTextX, armorTextY);
    }

    private void drawHeroBars() {
        float barX = camera.position.x - (width / 2) + 80;
        float barY = camera.position.y + (height / 2) - 58;

        float healthPercentage = (float) hero.getHealth() / hero.getMaxHealth();
        float armorPercentage = (float) hero.getArmor() / hero.getMaxArmor();

        shapeRenderer.setProjectionMatrix(camera.combined);
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
            b.update(delta);

            // Нова умова видалення
            if (b.shouldRemove()) {
                bullets.remove(i);
                continue;
            }

            // Існуюча логіка перевірки колізій...
            boolean bulletRemoved = false;

            for (Enemy e : enemies) {
                if (!b.isOpponent() && b.getBoundingRectangle().overlaps(e.getBoundingRectangle())) {
                    e.takeDamage(weapon.getDamage());
                    bullets.remove(i);
                    bulletRemoved = true;
                    break;
                }
            }

            if (!bulletRemoved && b.isOpponent() && b.getBoundingRectangle().overlaps(hero.getBoundingRectangle())) {
                hero.takeDamage(b.getEnemy().getDamage());
                bullets.remove(i);
                bulletRemoved = true;
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
            float weaponAngle = weapon.getAngle();
            float gunX = hero.getCenterX();
            float gunY = hero.getCenterY();
            bullets.add(new Bullet(gunX, gunY, weaponAngle, bulletTexture, false));
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
        shapeRenderer.dispose();
        hero.dispose();
        weapon.dispose();
        bulletTexture.dispose();
        enemyTexture.dispose();
        barBackgroundTexture.dispose();
        heartTexture.dispose();
        shieldTexture.dispose();
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }
}
