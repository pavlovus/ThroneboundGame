package com.mygdx.darkknight.effects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;
import com.mygdx.darkknight.enemies.Enemy; // Імпортуємо Enemy

public class HealingEffect {
    private Texture texture;
    private Enemy targetEnemy; // Посилання на ворога, на якого накладено ефект
    private float offsetX, offsetY; // Зміщення ефекту відносно центру ворога
    private float timer;
    private float duration; // Тривалість ефекту
    private float width, height; // Розміри ефекту
    private Vector2 currentPosition; // Поточна позиція ефекту (може рухатися вгору)

    public HealingEffect(Texture texture, Enemy targetEnemy, float width, float height, float duration) {
        this.texture = texture;
        this.targetEnemy = targetEnemy;
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.timer = duration;

        // Обчислюємо початкові зміщення для центрування відносно ворога
        this.offsetX = (targetEnemy.getWidth() / 2f) - (width / 2f);
        this.offsetY = (targetEnemy.getHeight() / 2f) - (height / 2f);

        // Ініціалізуємо поточну позицію на основі позиції ворога та зміщення
        this.currentPosition = new Vector2(
            targetEnemy.getX() + offsetX,
            targetEnemy.getY() + offsetY
        );
    }

    public void update(float delta) {
        timer -= delta;

        // Оновлюємо базову позицію ефекту, щоб вона слідувала за ворогом
        // Важливо: ми оновлюємо тільки базову X і Y від ворога, а потім додаємо рух вгору
        if (targetEnemy != null && !targetEnemy.isDead()) { // Перевірка, щоб уникнути NullPointerException, якщо ворог вже "видалився"
            currentPosition.x = targetEnemy.getX() + offsetX;

            // Додаємо рух вгору до Y координати
            // 15f - це максимальна відстань, на яку ефект підніметься. Можна налаштувати.
            currentPosition.y = (targetEnemy.getY() + offsetY) + ((duration - timer) / duration) * 15f;
        } else {
            // Якщо ворог мертвий або null, зупиняємо рух ефекту
            // Або можна відразу позначати його як finished
            timer = 0; // Це призведе до isFinished() == true
        }
    }

    public void draw(SpriteBatch batch) {
        if (isFinished()) {
            return;
        }
        if (targetEnemy.isDead()) { // Не малюємо ефект, якщо ворог мертвий
            return;
        }

        float alpha = timer / duration; // Розраховуємо прозорість (від 1 до 0)

        // Зберігаємо поточний запакований колір batch
        // Це дозволяє відновити його без впливу на наступні draw виклики
        float originalPackedColor = batch.getPackedColor();

        // Створюємо новий колір з потрібною прозорістю
        // Color.WHITE.toFloatBits() - це запакований float для білого кольору (RGB 1,1,1, Alpha 1)
        // Додаємо альфа-канал
        Color tempColor = new Color(1f, 1f, 1f, alpha); // Встановлюємо білий колір з потрібною прозорістю
        batch.setPackedColor(tempColor.toFloatBits()); // Встановлюємо запакований колір

        batch.draw(texture, currentPosition.x, currentPosition.y, width, height);

        // ВАЖЛИВО: Відновлюємо попередній запакований колір batch
        // Це запобігає впливу на прозорість інших об'єктів
        batch.setPackedColor(originalPackedColor);
    }

    public boolean isFinished() {
        // Ефект закінчується, якщо таймер вийшов АБО якщо цільовий ворог мертвий
        return timer <= 0 || (targetEnemy != null && targetEnemy.isDead());
    }
}
