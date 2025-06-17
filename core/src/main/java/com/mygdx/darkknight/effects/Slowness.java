package com.mygdx.darkknight.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class Slowness extends Effect {
    private int speedDebuff;
    private boolean applied = false;

    public Slowness(float duration, int speedDebuff, Texture texture) {
        super(duration, texture, "Slowness");
        this.speedDebuff = speedDebuff;
    }

    public Slowness(float duration) {
        super(duration, new Texture(Gdx.files.internal("core/assets/slowness.png")), "Slowness");
        this.speedDebuff = 200;
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.addSpeedBonus(-speedDebuff);  // додати негативний бонус
            applied = true;
        }
    }

    @Override
    protected void end(Hero hero) {
        if (applied) {
            hero.addSpeedBonus(speedDebuff); // повернути бонус назад
            applied = false;
        }
        icon.dispose();
    }
}
