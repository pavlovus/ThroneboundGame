package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.darkknight.Hero;

public class MeteorAI implements EnemyAI {
    private Rectangle roomBounds;
    
    public MeteorAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
    }
    
    @Override
    public void update(Enemy self, Hero hero, float delta) {
        // Логіка вже реалізована в класі Meteor
        // Цей клас потрібен лише для сумісності з інтерфейсом EnemyAI
    }
}
