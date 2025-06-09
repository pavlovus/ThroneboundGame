package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

public class Ghost extends Enemy {
    public Ghost(float x, float y, GameMap gameMap, Rectangle roomBounds) {
        super(Assets.ghostEnemyTexture, x, y, 32, 32, 150f, 1, 0, new GhostAI(roomBounds), gameMap);
    }

    @Override
    public void attack(Hero hero) {
        // Привид не атакує напряму, ефекти накладаються при зіткненні
        // Тому цей метод залишаємо порожнім
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Зберігаємо поточний колір батча
        Color oldColor = batch.getColor().cpy();

// Встановлюємо колір з прозорістю
        batch.setColor(1, 1, 1, getAlpha());

        // Малюємо привида
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());

        // Відновлюємо оригінальний колір
        batch.setColor(oldColor);
    }

    @Override
    public void move(float dx, float dy) {
        // Привид ігнорує колізії, тому просто змінюємо позицію напряму
        // без виклику методу базового класу, який перевіряє колізії
        setDirectPosition(getX() + dx, getY() + dy);
    }

    /**
     * Спеціальний метод для встановлення позиції без перевірки колізій
     */
    private void setDirectPosition(float x, float y) {
        // Викликаємо метод базового класу, але обходимо перевірку колізій
        // Це працює, тому що ми перевизначили метод move, який викликається в setPosition
        super.setPosition(x, y);
    }

    @Override
    public void setPosition(float x, float y) {
        // Перевизначаємо метод, щоб використовувати наш спеціальний метод
        setDirectPosition(x, y);
    }

    /**
     * Встановлює прозорість привида
     * @param alpha значення від 0 (повністю прозорий) до 1 (повністю видимий)
     */
    private float alpha = 1f; // Додаємо поле для зберігання прозорості

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    /**
     * Отримує поточну прозорість привида
     * @return значення прозорості від 0 до 1
     */
    public float getAlpha() {
        return alpha;
    }
}
