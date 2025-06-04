package com.mygdx.darkknight;

import com.badlogic.gdx.math.Vector2;

public class ShortAttackAI implements EnemyAI {
    private static final float ATTACK_RANGE = 60f;
    private static final float PATROL_RADIUS = 200f; // Збільшено радіус патрулювання
    private static final float RETREAT_HEALTH_THRESHOLD = 20f;
    private static final float ATTACK_COOLDOWN = 1.0f; // секунди
    private static final float DETECTION_RADIUS = 700f; // Зона уваги ворога

    private float attackCooldownTimer = 0f;
    private Vector2 patrolCenter = null;
    private Vector2 patrolTarget = null;

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        attackCooldownTimer -= delta;

        float dist = self.distanceTo(hero);

        if (patrolCenter == null) {
            patrolCenter = new Vector2(self.getCenterX(), self.getCenterY());
            setNewPatrolTarget();
        }

        if (self.getHealth() < RETREAT_HEALTH_THRESHOLD) {
            Vector2 dirAway = new Vector2(self.getCenterX() - hero.getCenterX(), self.getCenterY() - hero.getCenterY());
            dirAway.nor().scl(self.getSpeed() * delta);
            self.move(dirAway.x, dirAway.y);
            return;
        }

        if (dist > ATTACK_RANGE) {
            if (dist < DETECTION_RADIUS) {
                // Переслідуємо героя з повною швидкістю
                Vector2 dir = new Vector2(hero.getCenterX() - self.getCenterX(), hero.getCenterY() - self.getCenterY());
                dir.nor().scl(self.getSpeed() * delta);
                self.move(dir.x, dir.y);
            } else {
                // Патрулюємо з меншою швидкістю
                patrol(delta, self);
            }
        } else {
            if (attackCooldownTimer <= 0 && self.canAttack()) {
                self.attack(hero);
                attackCooldownTimer = ATTACK_COOLDOWN;
            }
        }
    }

    private void patrol(float delta, Enemy self) {
        if (patrolTarget == null || new Vector2(self.getCenterX(), self.getCenterY()).dst(patrolTarget) < 5f) {
            setNewPatrolTarget();
        }
        Vector2 dir = new Vector2(patrolTarget.x - self.getCenterX(), patrolTarget.y - self.getCenterY());
        dir.nor().scl(self.getSpeed() * 0.5f * delta); // Зменшена швидкість патрулювання (50%)
        self.move(dir.x, dir.y);
    }

    private void setNewPatrolTarget() {
        float angle = (float) (Math.random() * 2 * Math.PI);
        float radius = (float) (Math.random() * PATROL_RADIUS);
        float x = patrolCenter.x + radius * (float) Math.cos(angle);
        float y = patrolCenter.y + radius * (float) Math.sin(angle);
        patrolTarget = new Vector2(x, y);
    }
}
