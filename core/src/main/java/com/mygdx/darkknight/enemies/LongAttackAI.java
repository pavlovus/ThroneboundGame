package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.GameMap;

public class LongAttackAI implements EnemyAI {
    private static final float DESIRED_RANGE = 300f;
    private static final float RANGE_TOLERANCE = 50f;
    private static final float SHOOTING_MAX_RANGE = 450f; // Максимальна дистанція для стрільби
    private static final float STUCK_DETECTION_THRESHOLD = 1f;
    private static final float STUCK_REACTION_TIME = 0.5f;

    private Rectangle roomBounds;
    private Vector2 lastPosition;
    private float stuckTimer;
    private Vector2 avoidanceDirection;

    public LongAttackAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
        this.lastPosition = new Vector2();
        this.stuckTimer = 0f;
        this.avoidanceDirection = new Vector2();
    }

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        Vector2 enemyPos = self.getCenter();
        Vector2 heroPos = hero.getCenter();
        float distToHero = enemyPos.dst(heroPos);

        Vector2 targetDirection = new Vector2();
        boolean wantsToMove = true;

        if (distToHero > DESIRED_RANGE + RANGE_TOLERANCE) {
            targetDirection.set(heroPos).sub(enemyPos).nor();
        } else if (distToHero < DESIRED_RANGE - RANGE_TOLERANCE) {
            targetDirection.set(enemyPos).sub(heroPos).nor();
        } else {
            wantsToMove = false;
        }

        Vector2 finalMovementDirection = new Vector2(targetDirection);

        Vector2 testMoveAmount = new Vector2(targetDirection).scl(self.getSpeed() * delta);
        Rectangle projectedRect = new Rectangle(self.getX() + testMoveAmount.x, self.getY() + testMoveAmount.y, self.getWidth(), self.getHeight());

        boolean wallAhead = self.getGameMap().isCellBlocked(projectedRect);

        stuckTimer -= delta;
        if (stuckTimer <= 0) {
            if (lastPosition.dst(self.getX(), self.getY()) < STUCK_DETECTION_THRESHOLD) {
                avoidanceDirection.set(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).nor();
            }
            lastPosition.set(self.getX(), self.getY());
            stuckTimer = STUCK_REACTION_TIME;
        }

        if (wantsToMove) {
            if (wallAhead || lastPosition.dst(self.getX(), self.getY()) < STUCK_DETECTION_THRESHOLD * 2) {
                if (avoidanceDirection.isZero() || !wallAhead) {
                    if (MathUtils.randomBoolean()) {
                        avoidanceDirection.set(-targetDirection.y, targetDirection.x).nor();
                    } else {
                        avoidanceDirection.set(targetDirection.y, -targetDirection.x).nor();
                    }
                }
                finalMovementDirection.add(avoidanceDirection.scl(0.5f)).nor();
            } else {
                avoidanceDirection.setZero();
            }

            Vector2 actualMoveAmount = new Vector2(finalMovementDirection).scl(self.getSpeed() * delta);
            Rectangle finalProjectedRect = new Rectangle(self.getX() + actualMoveAmount.x, self.getY() + actualMoveAmount.y, self.getWidth(), self.getHeight());

            if (!self.getGameMap().isCellBlocked(finalProjectedRect)) {
                self.move(actualMoveAmount.x, actualMoveAmount.y);
            }
            // Якщо рух заблокований, self.move() не викликається, і ворог стоїть
        }

        // Обмеження позиції в межах кімнати
        float newX = self.getX();
        float newY = self.getY();
        newX = MathUtils.clamp(newX, roomBounds.x, roomBounds.x + roomBounds.width - self.getWidth());
        newY = MathUtils.clamp(newY, roomBounds.y, roomBounds.y + roomBounds.height - self.getHeight());
        self.setPosition(newX, newY);

        // --- Атака (стрільба) ---
        // Ворог стріляє, якщо герой знаходиться в межах SHOOTING_MAX_RANGE (незалежно від DESIRED_RANGE)
        // та є лінія видимості.
        if (distToHero <= SHOOTING_MAX_RANGE && self.canAttack() && self.getGameMap().hasLineOfSight(enemyPos, heroPos)) {
            ((LongAttackEnemy) self).attack(hero);
        }
    }
}
