package com.mygdx.darkknight.enemies; // Або com.mygdx.darkknight.effects, як я пропонував раніше

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.darkknight.Assets;

import java.util.List;

public class MeteorStrike {
    private static final float WARNING_DURATION = 1.5f; // Час попередження перед падінням
    private static final float FALL_DURATION = 0.5f;   // Час падіння метеорита
    private static final float EXPLOSION_DISPLAY_DURATION = 0.3f; // Час, протягом якого відображається вибух

    private Vector2 targetPosition; // Цільова позиція удару (центр зони ураження)
    private float timer; // Загальний таймер для фаз метеоритного удару
    private boolean isWarningPhase;
    private boolean isFallingPhase;
    private boolean isExplosionPhase; // Нова фаза для короткочасного відображення вибуху
    private boolean finished; // Чи завершився метеоритний удар і його можна видалити
    private boolean damageDealt; // Чи була вже нанесена шкода
    public boolean armorIgnore = true;

    private Texture warningTexture; // Текстура попереджувального кола
    private Texture explosionTexture;    // Текстура вибуху (замість fireTexture)
    private Rectangle hitArea; // Зона ураження метеорита

    private Hero hero; // Додано для можливості нанесення шкоди герою
    private GameMap gameMap; // Додано для перевірки колізій
    private int damage; // Шкода, яку наносить метеоритний удар

    // Нові змінні для анімації падіння
    private Vector2 startFallingPosition; // Початкова позиція метеорита для падіння
    private float initialMeteorScale = 0.5f; // Початковий розмір метеорита
    private float finalMeteorScale = 0.25f;  // Кінцевий розмір метеорита (можна налаштувати)
    private List<Bullet> bullets;
    private Music sound;


    public MeteorStrike(float x, float y, Hero hero, GameMap gameMap, Texture warningTexture, Texture explosionTexture, int damage, List<Bullet> bullets) {
        this.targetPosition = new Vector2(x, y);
        // Задаємо область ураження навколо цільової позиції
        this.hitArea = new Rectangle(x - 75, y - 75, 150, 150); // Можна налаштувати розмір зони ураження

        this.hero = hero;
        this.gameMap = gameMap;
        this.damage = damage;

        this.warningTexture = warningTexture;
        this.explosionTexture = explosionTexture;

        this.timer = 0;
        this.isWarningPhase = true;
        this.isFallingPhase = false;
        this.isExplosionPhase = false;
        this.finished = false;
        this.damageDealt = false;
        this.bullets = bullets;
        sound = Gdx.audio.newMusic(Gdx.files.internal("fireball.mp3"));

        // Встановлюємо початкову позицію для падіння вище цільової
        // Можна налаштувати висоту падіння (наприклад, 100-200 пікселів вище)
        this.startFallingPosition = new Vector2(targetPosition.x + 1000, targetPosition.y + 1000); // Налаштуйте ці значення для кута падіння
    }

    public void update(float delta) {
        timer += delta;

        if (isWarningPhase) {
            if (timer >= WARNING_DURATION) {
                isWarningPhase = false;
                isFallingPhase = true;
                timer = 0; // Скидаємо таймер для наступної фази
            }
        } else if (isFallingPhase) {
            // Оновлюємо позицію метеорита під час падіння
            if (timer >= FALL_DURATION) {
                isFallingPhase = false;
                isExplosionPhase = true;
                timer = 0; // Скидаємо таймер для фази вибуху
                dealDamage(); // Наносимо шкоду, коли метеорит досяг землі
            }
        } else if (isExplosionPhase) {
            if (timer >= EXPLOSION_DISPLAY_DURATION) {
                isExplosionPhase = false;
                finished = true;
            }
        }
    }

    // Метод для нанесення шкоди
    private void dealDamage() {
        sound.stop();
        sound.play();
        if (!damageDealt && hero != null) {
            // Перевіряємо, чи герой знаходиться в зоні ураження
            if (hero.getBoundingRectangle().overlaps(hitArea)) {
                hero.takeDamage(damage, armorIgnore);
                damageDealt = true; // Переконаємося, що шкода нанесена лише один раз
            }
        }
    }

    public void draw(SpriteBatch batch) {
        if (isWarningPhase) {
            // Миготіння попереджувальної текстури
            float alpha = 0.25f + (float) Math.sin(timer * 10) * 0.1f; // Миготіння
            Color oldColor = batch.getColor().cpy();
            batch.setColor(1, 1, 1, alpha);
            batch.draw(warningTexture, hitArea.x, hitArea.y, hitArea.width, hitArea.height);
            batch.setColor(oldColor);
        } else if (isFallingPhase) {
            // Малюємо метеорит, що падає, рухаючись від startFallingPosition до targetPosition
            float progress = timer / FALL_DURATION;
            Vector2 currentMeteorPos = new Vector2();
            currentMeteorPos.x = startFallingPosition.x + (targetPosition.x - startFallingPosition.x) * progress;
            currentMeteorPos.y = startFallingPosition.y + (targetPosition.y - startFallingPosition.y) * progress;

            float currentScale = initialMeteorScale + (finalMeteorScale - initialMeteorScale) * progress; // Збільшення розміру під час падіння

            // Використовуємо meteorTexture для падаючого метеорита
            batch.draw(Assets.meteorTexture, // Використовуємо Assets.meteorTexture
                currentMeteorPos.x - (Assets.meteorTexture.getWidth() * currentScale) / 2, // Центруємо по X
                currentMeteorPos.y - (Assets.meteorTexture.getHeight() * currentScale) / 2, // Центруємо по Y
                Assets.meteorTexture.getWidth() * currentScale,
                Assets.meteorTexture.getHeight() * currentScale);
        } else if (isExplosionPhase) {
            // Малюємо вибух (короткий час)
            float alpha = 1.0f - (timer / EXPLOSION_DISPLAY_DURATION); // Поступово згасає
            Color oldColor = batch.getColor().cpy();
            batch.setColor(1, 1, 1, alpha);

            // Налаштовуємо розмір вибуху
            float explosionScale = 0.2f + (timer / EXPLOSION_DISPLAY_DURATION) * 0.5f; // Початковий 0.2, зростає до 0.7
            // Налаштуйте ці значення, щоб вибух був втричі меншим
            float finalExplosionSize = 0.3f; // Максимальний розмір вибуху (0.7 від оригінальної текстури)
            float currentExplosionScale = 0.17f + (finalExplosionSize - 0.1f) * (timer / EXPLOSION_DISPLAY_DURATION);


            batch.draw(explosionTexture,
                targetPosition.x - (explosionTexture.getWidth() * currentExplosionScale) / 2,
                targetPosition.y - (explosionTexture.getHeight() * currentExplosionScale) / 2,
                explosionTexture.getWidth() * currentExplosionScale,
                explosionTexture.getHeight() * currentExplosionScale);
            batch.setColor(oldColor);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    private int getDamage(){return damage;}
}
