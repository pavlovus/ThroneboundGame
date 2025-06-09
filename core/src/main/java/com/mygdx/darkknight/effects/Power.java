package com.mygdx.darkknight.effects;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class Power extends Effect {
    private int damageBoost;
    private boolean applied = false;

    public Power(float duration, int damageBoost, Texture texture) {
        super(duration, texture);
        this.damageBoost = damageBoost;
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.getCurrentWeapon().setDamage(hero.getCurrentWeapon().getDamage() + damageBoost);
            applied = true;
        }
    }

    @Override
    protected void end(Hero hero) {
        hero.getCurrentWeapon().setDamage(hero.getCurrentWeapon().getDamage() - damageBoost);
        icon.dispose();
    }
}
