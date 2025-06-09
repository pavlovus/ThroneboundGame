package com.mygdx.darkknight.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class Slowness extends Effect {
    private int speedDebuff;
    private boolean applied = false;

    public Slowness(float duration, int speedDebuff, Texture texture) {
        super(duration, texture);
        this.speedDebuff = speedDebuff;
    }

    public Slowness(float duration) {
        super(duration, new Texture(Gdx.files.internal("slowness.png")));
        this.speedDebuff = 200; // Зменшення швидкості на 200 одиниць
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.setSpeed(hero.getSpeed() - speedDebuff);
            applied = true;
        }
    }

    @Override
    protected void end(Hero hero) {
        hero.setSpeed(hero.getSpeed() + speedDebuff);
        icon.dispose();
    }
}
