package com.mygdx.darkknight;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class LongAttackAI implements EnemyAI {
    private static final float DESIRED_RANGE = 1000f;
    private static final float RANGE_TOLERANCE = 250f;
    private Rectangle roomBounds;

    public LongAttackAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
    }

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        Vector2 enemyPos = self.getCenter();
        Vector2 heroPos = hero.getCenter();
        float dist = enemyPos.dst(heroPos);

        // Keep enemy within room boundaries
        if (!roomBounds.contains(enemyPos)) {
            Vector2 center = new Vector2(roomBounds.x + roomBounds.width / 2, roomBounds.y + roomBounds.height / 2);
            Vector2 moveDir = center.sub(enemyPos).nor();
            self.move(moveDir.x * self.getSpeed() * delta, moveDir.y * self.getSpeed() * delta);
            return;
        }

        // Movement logic
        if (dist > DESIRED_RANGE + RANGE_TOLERANCE) {
            moveTowards(heroPos, self, delta);
        } else if (dist < DESIRED_RANGE - RANGE_TOLERANCE) {
            moveAwayFrom(heroPos, self, delta);
        }

        // Attack only with clear line of sight
        if (self.canAttack() && isInRange(dist) && hasClearShot(self, hero)) {
            self.attack(hero);
        }
    }

    private boolean isInRange(float distance) {
        return distance >= (DESIRED_RANGE - RANGE_TOLERANCE)
                && distance <= (DESIRED_RANGE + RANGE_TOLERANCE);
    }

    private boolean hasClearShot(Enemy enemy, Hero hero) {
        Vector2 start = enemy.getCenter();
        Vector2 end = hero.getCenter();
        return enemy.getGameMap().hasLineOfSight(start, end);
    }

    private void moveTowards(Vector2 target, Enemy self, float delta) {
        Vector2 direction = new Vector2(target.x - self.getCenterX(), target.y - self.getCenterY());
        direction.nor().scl(self.getSpeed() * delta);
        self.move(direction.x, direction.y);
    }

    private void moveAwayFrom(Vector2 target, Enemy self, float delta) {
        Vector2 direction = new Vector2(self.getCenterX() - target.x, self.getCenterY() - target.y);
        direction.nor().scl(self.getSpeed() * delta);
        self.move(direction.x, direction.y);
    }
}
