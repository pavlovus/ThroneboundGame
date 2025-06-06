package com.mygdx.darkknight;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

import java.util.List;

public class ShortAttackAI implements EnemyAI {
    private static final float ATTACK_RANGE = 30f;
    private static final float DETECTION_RANGE = 400f;
    private static final float PATROL_SPEED_MODIFIER = 0.7f;

    private Rectangle roomBounds; // Межі кімнати для патрулювання
    private Vector2 spawnPoint; // Точка, навколо якої патрулює ворог
    private Vector2 currentPatrolTarget;
    float timeToChangeTarget = 0;
    private float stuckTime = 0;
    private static final float STUCK_THRESHOLD = 1.0f; // Час у секундах, після якого ворог вважається застряглим

    public ShortAttackAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
        this.spawnPoint = new Vector2(roomBounds.x + roomBounds.width / 2, roomBounds.y + roomBounds.height / 2);
        this.currentPatrolTarget = generateNewPatrolTarget();
    }

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        Vector2 enemyPos = self.getCenter();
        Vector2 heroPos = hero.getCenter();
        float distanceToPlayer = enemyPos.dst(heroPos);

        if (distanceToPlayer <= ATTACK_RANGE && hasLineOfSight(self, hero)) {
            if (self.canAttack()) {
                self.attack(hero);
            }
        } else if (distanceToPlayer <= DETECTION_RANGE && hasLineOfSight(self, hero)) {
            moveTowards(heroPos, self, delta);
        } else {
            patrol(self, delta);
        }
    }

    private void patrol(Enemy self, float delta) {
        Vector2 direction = new Vector2(
            currentPatrolTarget.x - self.getCenterX(),
            currentPatrolTarget.y - self.getCenterY()
        );

        float distanceToTarget = direction.len();

        if (distanceToTarget < 10f || isPathBlocked(self, direction)) {
            // Досягли цільової точки або шлях заблоковано - генеруємо нову
            currentPatrolTarget = generateNewPatrolTarget();
            timeToChangeTarget = 2f + MathUtils.random(3f);
        } else {
            // Рухаємось до цільової точки
            direction.nor().scl(self.getSpeed() * PATROL_SPEED_MODIFIER * delta);
            self.move(direction.x, direction.y);

            // Випадкова зміна цільової точки через час
            timeToChangeTarget -= delta;
            if (timeToChangeTarget <= 0) {
                currentPatrolTarget = generateNewPatrolTarget();
                timeToChangeTarget = 2f + MathUtils.random(3f);
            }
        }
    }

    private Vector2 generateNewPatrolTarget() {
        // Генеруємо випадкову точку в межах кімнати
        return new Vector2(
            MathUtils.random(roomBounds.x, roomBounds.x + roomBounds.width),
            MathUtils.random(roomBounds.y, roomBounds.y + roomBounds.height)
        );
    }

    private void moveTowards(Vector2 target, Enemy self, float delta) {
        Vector2 direction = new Vector2(target.x - self.getCenterX(), target.y - self.getCenterY());
        if (!isPathBlocked(self, direction)) {
            direction.nor().scl(self.getSpeed() * delta);
            self.move(direction.x, direction.y);

            // Перевіряємо, чи ворог застряг
            if (Math.abs(direction.x) < 0.1f && Math.abs(direction.y) < 0.1f) {
                stuckTime += delta;
            } else {
                stuckTime = 0; // Скидаємо час застрягання, якщо ворог рухається
            }

            // Якщо ворог застряг, змінюємо цільову точку
            if (stuckTime >= STUCK_THRESHOLD) {
                currentPatrolTarget = generateNewPatrolTarget();
                stuckTime = 0; // Скидаємо час застрягання
            }
        } else {
            // Якщо шлях заблоковано, змінюємо цільову точку
            currentPatrolTarget = generateNewPatrolTarget();
            stuckTime = 0; // Скидаємо час застрягання
        }
    }

    private boolean hasLineOfSight(Enemy enemy, Hero hero) {
        Vector2 start = enemy.getCenter();
        Vector2 end = hero.getCenter();
        return enemy.getGameMap().hasLineOfSight(start, end);
    }

    private boolean isPathBlocked(Enemy self, Vector2 direction) {
        Rectangle futureBounds = new Rectangle(
                self.getBoundingRectangle().x + direction.x,
                self.getBoundingRectangle().y + direction.y,
                self.getBoundingRectangle().width,
                self.getBoundingRectangle().height
        );
        return self.getGameMap().isCellBlocked(futureBounds);
    }
}
