package com.mygdx.darkknight.effects;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public abstract class Effect {
    protected float duration;
    protected float timeElapsed;
    protected Texture icon;
    protected Texture texture;
    protected String name;

    public Effect(float duration, Texture icon) {
        this.duration = duration;
        this.icon = icon;
        this.timeElapsed = 0;
    }

    public Effect(float duration, Texture icon, String name) {
        this.duration = duration;
        this.icon = icon;
        this.timeElapsed = 0;
        this.name = name;
    }

    public void update(Hero hero, float deltaTime) {
        if (timeElapsed == 0) {
            apply(hero, deltaTime);
        }
        timeElapsed += deltaTime;
        if (isExpired()) {
            end(hero);
        }
    }

    public boolean isExpired() {
        return timeElapsed >= duration;
    }

    public Texture getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public Texture getTexture() {
        return texture;
    }

    protected abstract void apply(Hero hero, float deltaTime);
    protected abstract void end(Hero hero);
}
