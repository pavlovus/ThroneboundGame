package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.util.List;

public class Ghost extends Enemy {
    private float alpha = 0.3f; // Початкова прозорість 30%
    protected boolean flip = false; // Нове поле для фліпу текстури

    public Ghost(Texture texture, float x, float y, GameMap gameMap, Rectangle roomBounds, List<Bullet> bullets) {
        super(texture, x, y, 45, 45, 225f, 1, 0, bullets, new GhostAI(roomBounds, bullets), gameMap, false);
        this.setAttackCooldown(0.1f);
    }

    @Override
    public void attack(Hero hero) {
        // Привид не атакує напряму, ефекти накладаються при зіткненні
    }

    @Override
    public void update(Hero hero, float delta) {
        super.update(hero, delta);
        // Встановлюємо фліп на основі позиції героя
        setFlip(getCenterX() < hero.getCenterX()); // Фліп, якщо герой праворуч
    }

    @Override
    public void draw(SpriteBatch batch) {
        // Зберігаємо поточний колір батча
        Color oldColor = batch.getColor().cpy();
        // Встановлюємо колір з прозорістю
        batch.setColor(1, 1, 1, alpha);
        // Використовуємо змінну flip для дзеркального відображення текстури
        batch.draw(texture, x, y, getWidth()/2f, getHeight()/2f, (float) getWidth(), (float) getHeight(), 1, 1, 0f, 0, 0, texture.getWidth(), texture.getHeight(), flip, false);
        // Малюємо індикатори шкоди
        GlyphLayout layout = new GlyphLayout();
        for (DamageIndicator indicator : damageIndicators) {
            layout.setText(damageFont, indicator.text);
            damageFont.setColor(1.0f, 1, 1, indicator.alpha);
            float textX = indicator.isRight ? x + getWidth() : x - layout.width * 2;
            textX += 13;
            float textY = y + getHeight() + indicator.yOffset;
            damageFont.draw(batch, indicator.text, textX, textY);
        }
        // Відновлюємо оригінальний колір
        batch.setColor(oldColor);
        damageFont.setColor(1f, 1f, 1f, 1f);
    }

    @Override
    public void move(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getAlpha() {
        return alpha;
    }

    // Нові методи для керування фліпом
    public void setFlip(boolean flip) {
        this.flip = flip;
    }

    public boolean isFlip() {
        return flip;
    }
}
