package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.GameMap; // Імпортуємо GameMap

import java.util.List;

public class HealerAI implements EnemyAI {
    private static final float SEARCH_RADIUS = 1000f; // Радіус пошуку ворогів
    private static final float KEEP_DISTANCE = 50f; // Дистанція до цілі
    private static final float RANDOM_MOVE_INTERVAL = 1.5f; // Інтервал зміни напрямку (1-2 секунди)
    private Rectangle roomBounds;
    private float randomMoveTimer = 0f; // Таймер для хаотичного руху
    private Vector2 randomDirection = new Vector2(0, 0); // Випадковий напрямок

    public HealerAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
    }

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        Vector2 direction = new Vector2();
        Vector2 selfPos = new Vector2(self.getCenterX(), self.getCenterY());

        // Перевіряємо, чи self є екземпляром Healer
        if (self instanceof Healer) {
            Healer healer = (Healer) self;
            List<Enemy> enemies = healer.getEnemies();
            GameMap gameMap = self.getGameMap(); // Отримуємо карту

            // Шукаємо ворога з найменшим здоров’ям
            Enemy target = null;
            int minHealth = Integer.MAX_VALUE;
            float minDistance = SEARCH_RADIUS;
            for (Enemy enemy : enemies) {
                if (enemy != self && !enemy.isDead()) {
                    Vector2 enemyPos = new Vector2(enemy.getCenterX(), enemy.getCenterY());
                    float distance = selfPos.dst(enemyPos);
                    if (distance < minDistance) {
                        if (enemy.getHealth() < minHealth) {
                            minHealth = enemy.getHealth();
                            target = enemy;
                            minDistance = distance;
                        }
                    }
                }
            }

            if (target != null) {
                Vector2 targetPos = new Vector2(target.getCenterX(), target.getCenterY());
                float distanceToTarget = selfPos.dst(targetPos);

                if (distanceToTarget > ((Healer) self).getHealRadius()) {
                    // Рухаємося до цілі, якщо вона занадто далеко для хілу
                    direction.set(targetPos).sub(selfPos).nor();
                } else if (distanceToTarget < KEEP_DISTANCE) {
                    // Відходимо від цілі, якщо вона занадто близько
                    direction.set(selfPos).sub(targetPos).nor();
                } else {
                    // Якщо ціль в радіусі хілу, але не занадто близько, стоїмо на місці, але рухаємося хаотично
                    randomMoveTimer -= delta;
                    if (randomMoveTimer <= 0) {
                        float angle = (float) (Math.random() * 2 * Math.PI);
                        randomDirection.set((float) Math.cos(angle), (float) Math.sin(angle));
                        randomMoveTimer = RANDOM_MOVE_INTERVAL + (float) Math.random() * 0.5f; // 1.5-2 секунди
                    }
                    direction.set(randomDirection);
                }
            } else {
                // Якщо немає цілі для хілу, рухаємося хаотично
                randomMoveTimer -= delta;
                if (randomMoveTimer <= 0) {
                    float angle = (float) (Math.random() * 2 * Math.PI);
                    randomDirection.set((float) Math.cos(angle), (float) Math.sin(angle));
                    randomMoveTimer = RANDOM_MOVE_INTERVAL + (float) Math.random() * 0.5f; // 1.5-2 секунди
                }
                direction.set(randomDirection);
            }

            // Обчислюємо нову позицію
            Vector2 velocity = direction.cpy().scl(self.getSpeed() * delta);
            float newX = self.getX() + velocity.x;
            float newY = self.getY() + velocity.y;

            // Перевіряємо колізії зі стінами перед рухом
            Rectangle proposedRect = new Rectangle(newX, newY, self.getWidth(), self.getHeight());
            if (gameMap != null && gameMap.isCellBlocked(proposedRect)) {
                // Якщо нова позиція заблокована, спробувати рухатися тільки по одній осі
                Rectangle testX = new Rectangle(newX, self.getY(), self.getWidth(), self.getHeight());
                Rectangle testY = new Rectangle(self.getX(), newY, self.getWidth(), self.getHeight());

                boolean canMoveX = !gameMap.isCellBlocked(testX);
                boolean canMoveY = !gameMap.isCellBlocked(testY);

                if (canMoveX && !canMoveY) {
                    self.setX(newX);
                } else if (canMoveY && !canMoveX) {
                    self.setY(newY);
                } else if (canMoveX && canMoveY) {
                    self.setX(newX);
                    self.setY(newY);
                }
                // Якщо обидві осі заблоковані або рух неможливий, просто не рухаємося.
            } else {
                // Якщо не заблоковано, рухаємося
                self.setX(newX);
                self.setY(newY);
            }

            // Перевіряємо межі кімнати (це вже є, але важливо, щоб було після колізій з картою)
            float clampedX = Math.max(roomBounds.x, Math.min(self.getX(), roomBounds.x + roomBounds.width - self.getWidth()));
            float clampedY = Math.max(roomBounds.y, Math.min(self.getY(), roomBounds.y + roomBounds.height - self.getHeight()));
            self.setX(clampedX);
            self.setY(clampedY);

        } else {
            direction.set(0, 0); // Якщо не Healer, стоїмо
        }
    }
}
