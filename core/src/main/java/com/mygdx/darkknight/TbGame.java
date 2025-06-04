package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class TbGame implements Screen {
    private GameMap gameMap;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private PauseMenu pauseMenu;
    private boolean isPaused = false;
    private Hero hero;
    private Weapon weapon;
    private Texture bulletTexture;
    private List<Bullet> bullets;

    private int width, height;
    private List<Enemy> enemies;

    @Override
    public void show() {
        pauseMenu = new PauseMenu(this);
        System.out.println("🔍 show() запущено");
        gameMap = new GameMap("FirstLevelMap.tmx");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        batch = new SpriteBatch();

        bullets = new ArrayList<>();

        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();


        bullets = new ArrayList<>();
        enemies = new ArrayList<>();
        Texture skeletonTexture = new Texture("core/assets/skeleton.png");
        Texture rangedTexture = new Texture("core/assets/skeleton.png");
        bulletTexture = new Texture("core/assets/bullet.png");

        enemies.add(new ShortAttackEnemy(skeletonTexture, 500, 300, 100, 100, 200f, 100, 1.5f));
        enemies.add(new LongAttackEnemy(rangedTexture, 800, 400, 100, 100, 180f, 80, 2.0f, bulletTexture, bullets));

        hero = new Hero("core/assets/hero.png", width / 2f, height / 2f); // початкова позиція героя в центрі екрану
        weapon = new Weapon("core/assets/gun.png");
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

            // Оновлення куль
            for (int i = bullets.size() - 1; i >= 0; i--) {
                Bullet b = bullets.get(i);
                b.update(delta);
                if (b.isOffScreen(width, height)) {
                    bullets.remove(i);
                }
            }
        }

        // Отримуємо позицію миші незалежно від паузи для правильного виведення зброї
        float mouseX = Gdx.input.getX();
        float mouseY = height - Gdx.input.getY();

        weapon.updateAngle(mouseX, mouseY, hero.getCenterX(), hero.getCenterY());

        // Оновлюємо камеру, щоб слідувала за героєм
        camera.position.set(hero.getCenterX(), hero.getCenterY(), 0);
        camera.update();

        // Очищаємо екран
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
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

        batch.end();

        // Якщо пауза активна — малюємо меню поверх
        if (isPaused) {
            pauseMenu.render();
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
            bullets.add(new Bullet(gunX, gunY, weaponAngle, bulletTexture));
        }
    }

    @Override
    public void resize(int width, int height) {
        // за потреби можна оновлювати viewport/camera
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
        hero.dispose();
        weapon.dispose();
        bulletTexture.dispose();
        gameMap.dispose(); // <<< ДОБАВЛЕНО звільнення ресурсів карти
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (!paused) {
            Gdx.input.setInputProcessor(null); // або твій InputProcessor, якщо треба
        }
    }
}
