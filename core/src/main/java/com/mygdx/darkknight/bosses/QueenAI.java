package com.mygdx.darkknight.bosses;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;
import com.mygdx.darkknight.enemies.EnemyAI;

public class QueenAI implements EnemyAI {
    private Rectangle roomBounds;

    public QueenAI(Rectangle roomBounds) {
        this.roomBounds = roomBounds;
    }

    @Override
    public void update(Enemy self, Hero hero, float delta) {
        // Королева залишається в центрі
        float centerX = roomBounds.x + roomBounds.width / 2 - self.getWidth() / 2;
        float centerY = roomBounds.y + roomBounds.height / 2 - self.getHeight() / 2;
        self.setPosition(centerX, centerY);
    }
}
