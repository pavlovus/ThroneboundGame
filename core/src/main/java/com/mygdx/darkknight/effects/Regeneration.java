package com.mygdx.darkknight.effects;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class Regeneration extends Effect {
    private int healthHealPerSecond;
    private boolean applied = false;
    private float timeSinceLastDamage = 0f;
    private float timePerHeal;

    public Regeneration(float duration, int healthHealPerSecond, float timePerHeal, Texture texture) {
        super(duration, texture);
        this.healthHealPerSecond = healthHealPerSecond;
        this.timePerHeal = timePerHeal;
    }

    public Regeneration(float duration, int healthDamagePerSecond, Texture texture) {
        super(duration, texture);
        this.healthHealPerSecond = healthDamagePerSecond;
        this.timePerHeal = 1f;
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.heal(healthHealPerSecond);
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

        if (timeSinceLastDamage >= timePerHeal) {
            hero.heal(healthHealPerSecond);
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
