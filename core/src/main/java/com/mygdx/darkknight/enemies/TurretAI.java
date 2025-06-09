package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.darkknight.Hero;

public class TurretAI implements EnemyAI {
    private Rectangle roomBounds;
    
    public TurretAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
    }
    
    @Override
    public void update(Enemy self, Hero hero, float delta) {
        // Логіка вже реалізована в класі Turret
        // Цей клас потрібен лише для сумісності з інтерфейсом EnemyAI
    }
}
