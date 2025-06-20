package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Hero;

public class ShortAttackAI implements EnemyAI {
    private static final float ATTACK_RANGE = 30f;
    private static final float DETECTION_RANGE = 400f;
    private static final float PATROL_SPEED_MODIFIER = 0.7f;
    private static final float WALL_AVOIDANCE_DISTANCE = 40f;
    private static final float DIRECTION_CHANGE_TIME = 0.5f;

    private Rectangle roomBounds;
    private Vector2 spawnPoint;
    private Vector2 currentPatrolTarget;
    private float timeToChangeTarget = 0;
    private float directionChangeTimer = 0;
    private Vector2 lastPosition = new Vector2();
    private Vector2 currentDirection = new Vector2();
    private Vector2 avoidanceDirection = new Vector2();
    private float lastDx = 0f; // Нове поле для збереження dx

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

        if (lastPosition.isZero()) {
            lastPosition.set(enemyPos);
        }

        if (distanceToPlayer <= ATTACK_RANGE && hasLineOfSight(self, hero)) {
            if (self.canAttack()) {
                self.attack(hero);
            }
        } else if (distanceToPlayer <= DETECTION_RANGE && hasLineOfSight(self, hero)) {
            pursueTarget(heroPos, self, delta);
        } else {
            patrol(self, delta);
        }

        lastPosition.set(enemyPos);
    }

    private void patrol(Enemy self, float delta) {
        Vector2 enemyPos = self.getCenter();

        float distanceToTarget = enemyPos.dst(currentPatrolTarget);
        if (distanceToTarget < 10f) {
            currentPatrolTarget = generateNewPatrolTarget();
            timeToChangeTarget = 2f + MathUtils.random(3f);
            return;
        }

        timeToChangeTarget -= delta;
        if (timeToChangeTarget <= 0) {
            currentPatrolTarget = generateNewPatrolTarget();
            timeToChangeTarget = 2f + MathUtils.random(3f);
        }

        moveWithWallAvoidance(self, currentPatrolTarget, PATROL_SPEED_MODIFIER, delta);
    }

    private Vector2 generateNewPatrolTarget() {
        return new Vector2(
            MathUtils.random(roomBounds.x + 50, roomBounds.x + roomBounds.width - 50),
            MathUtils.random(roomBounds.y + 50, roomBounds.y + roomBounds.height - 50)
        );
    }

    private void pursueTarget(Vector2 target, Enemy self, float delta) {
        moveWithWallAvoidance(self, target, 1.0f, delta);
    }

    private void moveWithWallAvoidance(Enemy self, Vector2 target, float speedModifier, float delta) {
        Vector2 enemyPos = self.getCenter();

        currentDirection.set(target).sub(enemyPos).nor();

        boolean wallAhead = checkWallAhead(self, currentDirection);

        if (wallAhead || directionChangeTimer > 0) {
            if (wallAhead && directionChangeTimer <= 0) {
                calculateAvoidanceDirection(self, currentDirection);
                directionChangeTimer = DIRECTION_CHANGE_TIME;
            }

            directionChangeTimer -= delta;

            Vector2 moveDirection = new Vector2(avoidanceDirection).scl(self.getSpeed() * speedModifier * delta);
            self.move(moveDirection.x, moveDirection.y);
            lastDx = moveDirection.x; // Оновлюємо lastDx
        } else {
            Vector2 moveDirection = new Vector2(currentDirection).scl(self.getSpeed() * speedModifier * delta);
            self.move(moveDirection.x, moveDirection.y);
            lastDx = moveDirection.x; // Оновлюємо lastDx
        }

        checkIfStuck(self, enemyPos, delta);
    }

    private boolean checkWallAhead(Enemy self, Vector2 direction) {
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
        Vector2[] testDirections = new Vector2[8];

        for (int i = 0; i < 8; i++) {
            float angle = i * 45f * MathUtils.degreesToRadians;
            testDirections[i] = new Vector2(MathUtils.cos(angle), MathUtils.sin(angle));
        }

        Vector2 bestDirection = null;
        float bestScore = -1;

        for (Vector2 dir : testDirections) {
            if (!checkWallAhead(self, dir)) {
                float similarity = dir.dot(originalDirection);
                if (similarity > bestScore) {
                    bestScore = similarity;
                    bestDirection = dir;
                }
            }
        }

        if (bestDirection != null) {
            avoidanceDirection.set(bestDirection);
        } else {
            avoidanceDirection.set(originalDirection).scl(-1);
        }
    }

    private void checkIfStuck(Enemy self, Vector2 currentPos, float delta) {
        if (currentPos.dst(lastPosition) < 0.5f * delta * self.getSpeed()) {
            currentPatrolTarget = generateNewPatrolTarget();
            avoidanceDirection.set(MathUtils.random(-1f, 1f), MathUtils.random(-1f, 1f)).nor();
            directionChangeTimer = DIRECTION_CHANGE_TIME;
        }
    }

    private boolean hasLineOfSight(Enemy enemy, Hero hero) {
        Vector2 start = enemy.getCenter();
        Vector2 end = hero.getCenter();
        return enemy.getGameMap().hasLineOfSight(start, end);
    }

    public float getLastDx() {
        return lastDx;
    }
}
