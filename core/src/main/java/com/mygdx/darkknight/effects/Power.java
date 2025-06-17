package com.mygdx.darkknight.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class Power extends Effect {
    private int damageBoost;
    private boolean applied = false;

    public Power(float duration, int damageBoost, Texture icon) {
        super(duration, icon, "Power");
        super.texture = new Texture(Gdx.files.internal("core/assets/power.png"));
        this.damageBoost = damageBoost;
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.getCurrentWeapon().addDamageBonus(damageBoost);
            applied = true;
        }
    }

    @Override
    protected void end(Hero hero) {
        hero.getCurrentWeapon().addDamageBonus(-damageBoost);
        applied = false;
        icon.dispose(); // Якщо текстура повторно не використовується
    }
}
