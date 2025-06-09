package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Hero;

public class TeleporterAI implements EnemyAI {
    private static final float ATTACK_RANGE = 40f;
    private Rectangle roomBounds;
    
    public TeleporterAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
    }
    
    @Override
    public void update(Enemy self, Hero hero, float delta) {
        // Працює тільки якщо телепортер не в процесі телепортації
        // (перевірка в класі Teleporter)
        Vector2 enemyPos = self.getCenter();
        Vector2 heroPos = hero.getCenter();
        float distanceToPlayer = enemyPos.dst(heroPos);
        
        if (distanceToPlayer <= ATTACK_RANGE && hasLineOfSight(self, hero)) {
            if (self.canAttack()) {
                self.attack(hero);
            }
        } else {
            moveTowards(heroPos, self, delta);
        }
    }
    
    private void moveTowards(Vector2 target, Enemy self, float delta) {
        Vector2 direction = new Vector2(target.x - self.getCenterX(), target.y - self.getCenterY());
        
        if (direction.len() > 0) {
            direction.nor().scl(self.getSpeed() * delta);
            self.move(direction.x, direction.y);
        }
    }
    
    private boolean hasLineOfSight(Enemy enemy, Hero hero) {
        Vector2 start = enemy.getCenter();
        Vector2 end = hero.getCenter();
        return enemy.getGameMap().hasLineOfSight(start, end);
    }
}
