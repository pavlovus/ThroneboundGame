package com.mygdx.darkknight.effects;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class Poison extends Effect {
    private int healthDamagePerSecond;
    private boolean applied = false;
    private float timeSinceLastDamage = 0f;
    private float timePerDamage;

    public Poison(float duration, int healthDamagePerSecond, float timePerDamage, Texture texture) {
        super(duration, texture);
        this.healthDamagePerSecond = healthDamagePerSecond;
        this.timePerDamage = timePerDamage;
    }

    public Poison(float duration, int healthDamagePerSecond, Texture texture) {
        super(duration, texture);
        this.healthDamagePerSecond = healthDamagePerSecond;
        this.timePerDamage = 1f;
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.takeDamage(healthDamagePerSecond);
            applied = true;
        }
    }

    @Override
    public void update(Hero hero, float deltaTime){
        if (timeElapsed == 0) {
            apply(hero, deltaTime);
        }
        timeElapsed += deltaTime;
        timeSinceLastDamage += deltaTime;

        if (timeSinceLastDamage >= timePerDamage) {
            hero.takeDamage(healthDamagePerSecond);
            timeSinceLastDamage = 0f;
        }
        if (isExpired()) {
            end(hero);
        }
    }


    @Override
    protected void end(Hero hero) {
        icon.dispose();
    }
}
