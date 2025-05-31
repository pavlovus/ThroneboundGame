package com.mygdx.darkknight;

import com.badlogic.gdx.math.Vector2;

public class LongAttackAI implements EnemyAI {
    private static final float DESIRED_RANGE = 1000f;
    private static final float RANGE_TOLERANCE = 250f;

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        float dist = self.distanceTo(hero);

        if (dist > DESIRED_RANGE + RANGE_TOLERANCE) {
            // Підходимо ближче
            Vector2 dir = new Vector2(hero.getCenterX() - self.getCenterX(), hero.getCenterY() - self.getCenterY());
            dir.nor().scl(self.getSpeed() * delta);
            self.move(dir.x, dir.y);
        } else if (dist < DESIRED_RANGE - RANGE_TOLERANCE) {
            // Відходимо назад
            Vector2 dir = new Vector2(self.getCenterX() - hero.getCenterX(), self.getCenterY() - hero.getCenterY());
            dir.nor().scl(self.getSpeed() * delta);
            self.move(dir.x, dir.y);
        } else if (self.canAttack()) {
            self.attack(hero);
        }
    }
}
