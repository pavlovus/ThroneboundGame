package com.mygdx.darkknight;

import com.badlogic.gdx.math.Vector2;

public class ShortAttackAI implements EnemyAI {
    private static final float ATTACK_RANGE = 60f;

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        float dist = self.distanceTo(hero);
        if (dist > ATTACK_RANGE) {
            Vector2 dir = new Vector2(hero.getCenterX() - self.getCenterX(), hero.getCenterY() - self.getCenterY());
            dir.nor().scl(self.getSpeed() * delta);
            self.move(dir.x, dir.y);
        } else if (self.canAttack()) {
            self.attack(hero);
        }
    }
}
