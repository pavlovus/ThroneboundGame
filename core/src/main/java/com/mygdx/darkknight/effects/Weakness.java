package com.mygdx.darkknight.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class Weakness extends Effect {
    private int damageDebuff;
    private boolean applied = false;

    public Weakness(float duration, int damageDebuff, Texture texture) {
        super(duration, texture, "Weakness");
        this.damageDebuff = damageDebuff;
    }

    public Weakness(float duration) {
        super(duration, new Texture(Gdx.files.internal("core/assets/weakness.png")), "Weakness");
        this.damageDebuff = 1;
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.getCurrentWeapon().addDamageBonus(-damageDebuff);  // зменшити шкоду
            applied = true;
        }
    }

    @Override
    protected void end(Hero hero) {
        if (applied) {
            hero.getCurrentWeapon().addDamageBonus(damageDebuff);  // відновити шкоду
            applied = false;
        }
        icon.dispose();
    }
}

