package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.effects.Effect;
import com.mygdx.darkknight.effects.Poison;
import com.mygdx.darkknight.effects.Slowness;
import com.mygdx.darkknight.effects.Weakness;

public class GhostAI implements EnemyAI {
    private static final float EFFECT_DURATION = 5f; // Тривалість ефектів
    private static final float MIN_ALPHA = 0.3f; // Мінімальна прозорість (30%)
    private static final float MAX_ALPHA = 0.7f; // Максимальна прозорість (70%)
    private static final float ALPHA_CHANGE_SPEED = 0.5f; // Швидкість зміни прозорості

    private Rectangle roomBounds;
    private boolean increasingAlpha = true; // Чи збільшується прозорість

    public GhostAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
    }

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        // 1. Рух до героя
        Vector2 direction = new Vector2(hero.getCenterX() - self.getCenterX(), hero.getCenterY() - self.getCenterY()).nor();
        float dx = direction.x * self.getSpeed() * delta;
        float dy = direction.y * self.getSpeed() * delta;

        // Тепер викликаємо перевизначений метод move() з Ghost, який ігнорує колізії
        self.move(dx, dy); // <<< ЗМІНА ТУТ

        // Обмежуємо позицію в межах кімнати (привид не має вилітати за межі ігрового поля)
        float newX = self.getX();
        float newY = self.getY();

        newX = MathUtils.clamp(newX, roomBounds.x, roomBounds.x + roomBounds.width - self.getWidth());
        newY = MathUtils.clamp(newY, roomBounds.y, roomBounds.y + roomBounds.height - self.getHeight());

        // Встановлюємо остаточну позицію після обмеження в межах кімнати
        // Це важливо, якщо roomBounds менші за карту, щоб привид не вилетів.
        self.setPosition(newX, newY); // >>> Це має викликати простий Enemy.setPosition або Ghost.setPosition, якщо він такий же простий

        // 2. Зміна прозорості
        float currentAlpha = ((Ghost) self).getAlpha();
        if (increasingAlpha) {
            currentAlpha += ALPHA_CHANGE_SPEED * delta;
            if (currentAlpha >= MAX_ALPHA) {
                currentAlpha = MAX_ALPHA;
                increasingAlpha = false;
            }
        } else {
            currentAlpha -= ALPHA_CHANGE_SPEED * delta;
            if (currentAlpha <= MIN_ALPHA) {
                currentAlpha = MIN_ALPHA;
                increasingAlpha = true;
            }
        }
        ((Ghost) self).setAlpha(currentAlpha);

        // 3. Накладання ефекту при контакті
        if (hero.getBoundingRectangle().overlaps(self.getBoundingRectangle()) && self.canAttack()) {
            applyRandomEffect(hero);
            self.resetAttackCooldown(); // Скидаємо кулдаун для ефекту

            // Після накладання ефекту, привид зникає (телепортується в випадкову позицію в межах кімнати)
            // і знову починає рухатися до героя.
            teleportToRandomPosition(self);
            ((Ghost) self).setAlpha(MIN_ALPHA); // Робимо його знову напівпрозорим після телепортації
        }
    }

    /**
     * Накладає випадковий ефект на гравця
     */
    private void applyRandomEffect(Hero hero) {
        int effectType = MathUtils.random(0, 2); // 0-2 для трьох типів ефектів
        Effect effect = null;

        switch (effectType) {
            case 0:
                effect = new Poison(EFFECT_DURATION, 1, 1f, new Texture(Gdx.files.internal("poison.png")));
                break;
            case 1:
                effect = new Slowness(EFFECT_DURATION, 200, new Texture(Gdx.files.internal("slowness.png")));
                break;
            case 2:
                effect = new Weakness(EFFECT_DURATION, 1, new Texture(Gdx.files.internal("weakness.png")));
                break;
        }
        if (effect != null) {
            hero.addEffect(effect);
        }
    }

    /**
     * Телепортує привида в випадкову допустиму позицію в межах кімнати
     */
    private void teleportToRandomPosition(Enemy self) {
        final int MAX_ATTEMPTS = 100;
        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            float randomX = roomBounds.x + MathUtils.random(0, roomBounds.width - self.getWidth());
            float randomY = roomBounds.y + MathUtils.random(0, roomBounds.height - self.getHeight());
            Vector2 newPos = new Vector2(randomX, randomY);

            // Оскільки привид пролітає крізь стіни, нам не потрібно перевіряти map.isCellBlocked()
            // Просто перевіряємо, чи позиція знаходиться в межах кімнати
            if (roomBounds.contains(newPos.x, newPos.y)) {
                self.setPosition(newPos.x, newPos.y);
                return;
            }
        }
        // Якщо не вдалося знайти випадкову позицію після багатьох спроб,
        // просто перемістимо його в центр кімнати.
        self.setPosition(roomBounds.x + roomBounds.width / 2, roomBounds.y + roomBounds.height / 2);
    }
}
