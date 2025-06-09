package com.mygdx.darkknight.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class Weakness extends Effect {
    private int damageDebuff;
    private boolean applied = false;

    public Weakness(float duration, int damageDebuff, Texture texture) {
        super(duration, texture);
        this.damageDebuff = damageDebuff;
    }
    
    public Weakness(float duration) {
        super(duration, new Texture(Gdx.files.internal("weakness.png")));
        this.damageDebuff = 1; // Зменшення шкоди на 1 одиницю
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.getCurrentWeapon().setDamage(hero.getCurrentWeapon().getDamage() - damageDebuff);
            applied = true;
        }
    }

    @Override
    protected void end(Hero hero) {
        hero.getCurrentWeapon().setDamage(hero.getCurrentWeapon().getDamage() + damageDebuff);
        icon.dispose();
    }
}
