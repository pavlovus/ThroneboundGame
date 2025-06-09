package com.mygdx.darkknight.enemies;

import com.mygdx.darkknight.Hero;

public interface EnemyAI {
    void update(Enemy self, Hero hero, float delta);
}
