package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.darkknight.Assets;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.util.List;

public class Ghost extends Enemy {
    private float alpha = 0.3f; // Початкова прозорість 30%

    public Ghost(Texture texture, float x, float y, GameMap gameMap, Rectangle roomBounds, List<Bullet> bullets) {
        // Збільшимо швидкість в 1.5 рази. Базова швидкість 150f, отже 150 * 1.5 = 225f
        super(texture, x, y, 32, 32, 225f, 1, 0, bullets, new GhostAI(roomBounds, bullets), gameMap, false);
        this.setAttackCooldown(0.1f);
    }

    @Override
    public void attack(Hero hero) {
        // Привид не атакує напряму, ефекти накладаються при зіткненні
        // Цей метод залишаємо порожнім. Логіка накладання ефектів в GhostAI.
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Зберігаємо поточний колір батча
        Color oldColor = batch.getColor().cpy();

        // Встановлюємо колір з прозорістю
        batch.setColor(1, 1, 1, alpha);

        // Малюємо привида
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());

        // Відновлюємо оригінальний колір
        batch.setColor(oldColor);
    }

    /**
     * ПЕРЕВИЗНАЧАЄМО МЕТОД RUCHu, щоб привид ігнорував стіни.
     * Він просто оновлює свої координати без перевірок gameMap.isCellBlocked.
     */
    @Override
    public void move(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Встановлює прозорість привида
     * @param alpha значення від 0 (повністю прозорий) до 1 (повністю видимий)
     */
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getAlpha() {
        return alpha;
    }
}
