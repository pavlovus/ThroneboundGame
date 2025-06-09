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
    private static final float TELEPORT_COOLDOWN = 5f; // Час між телепортаціями
    private static final float EFFECT_DURATION = 5f; // Тривалість ефектів
    private static final float VISIBILITY_CHANGE_TIME = 2f; // Час зміни видимості
    
    private Rectangle roomBounds;
    private float teleportTimer = 0f;
    private float visibilityTimer = 0f;
    private float currentAlpha = 1.0f; // Поточна прозорість (1.0 = повністю видимий)
    private boolean fadingIn = false; // Чи стає привид більш видимим
    
    public GhostAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
        // Одразу телепортуємо привида при створенні
        teleportTimer = 0f;
    }
    
    @Override
    public void update(Enemy self, Hero hero, float delta) {
        // Оновлюємо таймери
        teleportTimer -= delta;
        visibilityTimer -= delta;
        
        // Оновлюємо видимість привида
        updateVisibility(self, delta);
        
        // Перевіряємо, чи час телепортуватися
        if (teleportTimer <= 0) {
            teleportToRandomLocation(self);
            teleportTimer = TELEPORT_COOLDOWN;
        }
        
        // Рухаємося прямо до гравця (крізь стіни)
        moveTowardsHero(self, hero, delta);
        
        // Перевіряємо зіткнення з гравцем
        if (self.getBoundingRectangle().overlaps(hero.getBoundingRectangle())) {
            applyRandomEffect(hero);
            // Привид зникає після зіткнення (помирає)
            self.takeDamage(1);
        }
    }
    
    /**
     * Телепортує привида у випадкове місце в межах кімнати
     */
    private void teleportToRandomLocation(Enemy self) {
        float x = MathUtils.random(roomBounds.x, roomBounds.x + roomBounds.width - self.getWidth());
        float y = MathUtils.random(roomBounds.y, roomBounds.y + roomBounds.height - self.getHeight());
        
        // Встановлюємо нову позицію без перевірки колізій
        self.setPosition(x, y);
        
        // Починаємо з'являтися
        fadingIn = true;
        currentAlpha = 0.1f;
        visibilityTimer = VISIBILITY_CHANGE_TIME;
    }
    
    /**
     * Оновлює видимість привида (прозорість)
     */
    private void updateVisibility(Enemy self, float delta) {
        if (visibilityTimer <= 0) {
            // Змінюємо напрямок зміни прозорості
            fadingIn = !fadingIn;
            visibilityTimer = VISIBILITY_CHANGE_TIME;
        }
        
        // Змінюємо прозорість
        if (fadingIn) {
            currentAlpha += delta / VISIBILITY_CHANGE_TIME;
            if (currentAlpha > 1.0f) currentAlpha = 1.0f;
        } else {
            currentAlpha -= delta / VISIBILITY_CHANGE_TIME;
            if (currentAlpha < 0.2f) currentAlpha = 0.2f; // Не робимо повністю невидимим
        }
        
        // Встановлюємо прозорість привида
        if (self instanceof Ghost) {
            ((Ghost) self).setAlpha(currentAlpha);
        }
    }
    
    /**
     * Рухає привида прямо до гравця, ігноруючи перешкоди
     */
    private void moveTowardsHero(Enemy self, Hero hero, float delta) {
        Vector2 ghostPos = self.getCenter();
        Vector2 heroPos = hero.getCenter();
        
        // Обчислюємо напрямок до гравця
        Vector2 direction = new Vector2(heroPos).sub(ghostPos).nor();
        
        // Рухаємося до гравця з постійною швидкістю
        float moveSpeed = self.getSpeed() * delta;
        
        // Використовуємо спеціальний метод для руху крізь стіни
        moveGhostThroughWalls(self, direction.x * moveSpeed, direction.y * moveSpeed);
    }
    
    /**
     * Спеціальний метод для руху привида крізь стіни
     */
    private void moveGhostThroughWalls(Enemy self, float dx, float dy) {
        // Просто змінюємо позицію без перевірки колізій
        float newX = self.getX() + dx;
        float newY = self.getY() + dy;
        
        // Перевіряємо лише межі кімнати
        if (newX < roomBounds.x) newX = roomBounds.x;
        if (newX > roomBounds.x + roomBounds.width - self.getWidth()) 
            newX = roomBounds.x + roomBounds.width - self.getWidth();
        if (newY < roomBounds.y) newY = roomBounds.y;
        if (newY > roomBounds.y + roomBounds.height - self.getHeight()) 
            newY = roomBounds.y + roomBounds.height - self.getHeight();
        
        // Встановлюємо нову позицію
        self.setPosition(newX, newY);
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
}
