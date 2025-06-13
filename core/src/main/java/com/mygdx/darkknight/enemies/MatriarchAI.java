package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.darkknight.Hero;

public class MatriarchAI implements EnemyAI {
    private Rectangle roomBounds;

    private Vector2 circleCenter;
    private float circleRadius;
    private float currentAngle;
    private float rotationSpeed;

    private static final float COLLISION_PUSH_FORCE = 10f; // Сила, з якою відштовхуємося від стіни

    public MatriarchAI(Rectangle roomBounds, Vector2 initialPosition) {
        this.roomBounds = roomBounds;
        this.circleCenter = new Vector2(roomBounds.x + roomBounds.width / 2, roomBounds.y + roomBounds.height / 2);
        // Забезпечимо, що радіус дозволяє матці рухатися в межах кімнати
        // circleRadius має бути менше, ніж половина найменшої сторони кімнати, мінус половина розміру матки.
        this.circleRadius = Math.min(roomBounds.width / 2 - 96/2, roomBounds.height / 2 - 96/2) * 0.8f; // 0.8f для запасу
        if (this.circleRadius < 50f) this.circleRadius = 50f; // Мінімальний радіус

        this.rotationSpeed = 0.3f; // Зменшимо швидкість обертання для більш плавного руху
        this.currentAngle = MathUtils.atan2(initialPosition.y - circleCenter.y, initialPosition.x - circleCenter.x);
    }

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        currentAngle += rotationSpeed * delta;

        float targetX = circleCenter.x + circleRadius * MathUtils.cos(currentAngle);
        float targetY = circleCenter.y + circleRadius * MathUtils.sin(currentAngle);

        // Обмежуємо цільову позицію в межах кімнати, враховуючи розмір матки
        targetX = MathUtils.clamp(targetX, roomBounds.x, roomBounds.x + roomBounds.width - self.getWidth());
        targetY = MathUtils.clamp(targetY, roomBounds.y, roomBounds.y + roomBounds.height - self.getHeight());

        Vector2 currentPosition = new Vector2(self.getX(), self.getY());
        Vector2 desiredMovement = new Vector2(targetX, targetY).sub(currentPosition);
        float distance = desiredMovement.len();

        if (distance > 0.01f) { // Перевіряємо, чи є значний рух
            desiredMovement.nor().scl(self.getSpeed() * delta);

            // Спрощена логіка обходу стін:
            // Спробуємо рухатись спочатку по X, потім по Y, перевіряючи колізії.
            // Якщо є колізія, спробуємо "відштовхнутися" або змінити напрямок.

            float dx = desiredMovement.x;
            float dy = desiredMovement.y;

            Rectangle nextXRect = self.getBoundingRectangle();
            nextXRect.x += dx;

            boolean blockedX = self.getGameMap().isCellBlocked(nextXRect);

            Rectangle nextYRect = self.getBoundingRectangle();
            nextYRect.y += dy;

            boolean blockedY = self.getGameMap().isCellBlocked(nextYRect);

            if (!blockedX) {
                self.move(dx, 0);
            } else {
                // Якщо рух по X заблоковано, спробуємо зміститися по Y
                // або просто зупинитися по X і дозволити рух по Y
            }

            if (!blockedY) {
                self.move(0, dy);
            } else {
                // Якщо рух по Y заблоковано, спробуємо зміститися по X
            }

            // Додаткова логіка для відштовхування, якщо матка застрягла
            Rectangle currentRect = self.getBoundingRectangle();
            if (self.getGameMap().isCellBlocked(currentRect)) {
                // Матка в стіні, відштовхнемо її
                // Знаходимо центр стіни, від якої відштовхнутися
                // Для простоти, відштовхуємося від центру кімнати
                Vector2 pushDirection = self.getCenter().cpy().sub(circleCenter).nor().scl(COLLISION_PUSH_FORCE * delta);
                self.move(pushDirection.x, pushDirection.y);
            }
        }
    }
}
