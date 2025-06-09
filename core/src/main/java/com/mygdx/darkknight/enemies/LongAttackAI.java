package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Hero;

public class LongAttackAI implements EnemyAI {
    private static final float DESIRED_RANGE = 400f;
    private static final float RANGE_TOLERANCE = 100f;
    private static final float WALL_AVOIDANCE_DISTANCE = 40f;
    private static final float DIRECTION_CHANGE_TIME = 0.5f;
    
    private Rectangle roomBounds;
    private Vector2 lastPosition = new Vector2();
    private Vector2 currentDirection = new Vector2();
    private Vector2 avoidanceDirection = new Vector2();
    private float directionChangeTimer = 0;

    public LongAttackAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
    }

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        Vector2 enemyPos = self.getCenter();
        Vector2 heroPos = hero.getCenter();
        float dist = enemyPos.dst(heroPos);

        // Зберігаємо поточну позицію для виявлення застрягання
        if (lastPosition.isZero()) {
            lastPosition.set(enemyPos);
        }

        // Тримаємо ворога в межах кімнати
        if (!roomBounds.contains(enemyPos)) {
            Vector2 center = new Vector2(roomBounds.x + roomBounds.width / 2, roomBounds.y + roomBounds.height / 2);
            moveWithWallAvoidance(self, center, 1.0f, delta);
            lastPosition.set(enemyPos);
            return;
        }

        // Логіка руху з урахуванням дистанції до гравця
        if (dist > DESIRED_RANGE + RANGE_TOLERANCE) {
            // Наближаємось до гравця
            moveWithWallAvoidance(self, heroPos, 1.0f, delta);
        } else if (dist < DESIRED_RANGE - RANGE_TOLERANCE) {
            // Відходимо від гравця
            Vector2 awayDirection = new Vector2(enemyPos).sub(heroPos).nor();
            Vector2 targetPosition = new Vector2(enemyPos).add(awayDirection.scl(100f));
            moveWithWallAvoidance(self, targetPosition, 1.0f, delta);
        }

        // Атакуємо тільки якщо є чиста лінія вогню
        if (self.canAttack() && hasClearShot(self, hero)) {
            self.attack(hero);
        }
        
        // Оновлюємо останню позицію
        lastPosition.set(enemyPos);
    }

    private boolean hasClearShot(Enemy enemy, Hero hero) {
        Vector2 start = enemy.getCenter();
        Vector2 end = hero.getCenter();
        return enemy.getGameMap().hasLineOfSight(start, end);
    }

    private void moveWithWallAvoidance(Enemy self, Vector2 target, float speedModifier, float delta) {
        Vector2 enemyPos = self.getCenter();
        
        // Основний напрямок руху до цілі
        currentDirection.set(target).sub(enemyPos).nor();
        
        // Перевіряємо наявність стін попереду
        boolean wallAhead = checkWallAhead(self, currentDirection);
        
        // Якщо попереду стіна або таймер зміни напрямку активний
        if (wallAhead || directionChangeTimer > 0) {
            if (wallAhead && directionChangeTimer <= 0) {
                // Обчислюємо напрямок для обходу стіни
                calculateAvoidanceDirection(self, currentDirection);
                directionChangeTimer = DIRECTION_CHANGE_TIME;
            }
            
            // Зменшуємо таймер
            directionChangeTimer -= delta;
            
            // Рухаємось у напрямку обходу
            Vector2 moveDirection = new Vector2(avoidanceDirection).scl(self.getSpeed() * speedModifier * delta);
            self.move(moveDirection.x, moveDirection.y);
        } else {
            // Рухаємось до цілі
            Vector2 moveDirection = new Vector2(currentDirection).scl(self.getSpeed() * speedModifier * delta);
            self.move(moveDirection.x, moveDirection.y);
        }
        
        // Перевіряємо, чи ворог застряг
        checkIfStuck(self, enemyPos, delta);
    }
    
    private boolean checkWallAhead(Enemy self, Vector2 direction) {
        // Перевіряємо наявність стіни на відстані WALL_AVOIDANCE_DISTANCE
        Vector2 enemyPos = self.getCenter();
        Vector2 checkPoint = new Vector2(
            enemyPos.x + direction.x * WALL_AVOIDANCE_DISTANCE,
            enemyPos.y + direction.y * WALL_AVOIDANCE_DISTANCE
        );
        
        Rectangle checkRect = new Rectangle(
            checkPoint.x - self.getWidth() / 2f,
            checkPoint.y - self.getHeight() / 2f,
            self.getWidth(),
            self.getHeight()
        );
        
        return self.getGameMap().isCellBlocked(checkRect);
    }
    
    private void calculateAvoidanceDirection(Enemy self, Vector2 originalDirection) {
        // Пробуємо різні напрямки для обходу стіни
        Vector2[] testDirections = new Vector2[8];
        
        // Створюємо 8 напрямків навколо ворога (кожні 45 градусів)
        for (int i = 0; i < 8; i++) {
            float angle = i * 45f * MathUtils.degreesToRadians;
            testDirections[i] = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle));
        }
        
        // Знаходимо найкращий напрямок (без стіни і найближчий до оригінального)
        Vector2 bestDirection = null;
        float bestScore = -1;
        
        for (Vector2 dir : testDirections) {
            if (!checkWallAhead(self, dir)) {
                // Обчислюємо схожість з оригінальним напрямком (dot product)
                float similarity = dir.dot(originalDirection);
                
                // Вибираємо напрямок, який найбільш схожий на оригінальний
                if (similarity > bestScore) {
                    bestScore = similarity;
                    bestDirection = dir;
                }
            }
        }
        
        // Якщо знайдено підходящий напрямок, використовуємо його
        if (bestDirection != null) {
            avoidanceDirection.set(bestDirection);
        } else {
            // Якщо всі напрямки заблоковані, рухаємось у протилежному напрямку
            avoidanceDirection.set(originalDirection).scl(-1);
        }
    }
    
    private void checkIfStuck(Enemy self, Vector2 currentPos, float delta) {
        // Якщо ворог майже не рухається, змінюємо напрямок
        if (currentPos.dst(lastPosition) < 0.5f * delta * self.getSpeed()) {
            // Змінюємо напрямок обходу на випадковий
            avoidanceDirection.set(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).nor();
            directionChangeTimer = DIRECTION_CHANGE_TIME;
        }
    }
}
