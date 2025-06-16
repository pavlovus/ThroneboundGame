package com.mygdx.darkknight.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class Swiftness extends Effect{
    private int speedBoost;
    private boolean applied = false;

    public Swiftness(float duration, int speedBoost, Texture texture) {
        super(duration, texture, "Swiftness");
        super.texture = new Texture(Gdx.files.internal("core/assets/swiftness.png"));
        this.speedBoost = speedBoost;
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.setSpeed(hero.getSpeed() + speedBoost);
            applied = true;
        }
    }

    @Override
    protected void end(Hero hero) {
        hero.setSpeed(hero.getSpeed() - speedBoost);
        icon.dispose();
    }
}
