package com.mygdx.darkknight.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Ghost;
import com.mygdx.darkknight.enemies.GhostAI;

import java.util.List;

public class Poison extends Effect {
    private int healthDamagePerSecond;
    private boolean applied = false;
    private float timeSinceLastDamage = 0f;
    private float timePerDamage;
    private List<Bullet> bullets;
    private boolean armorIgnore = true;

    public Poison(float duration, int healthDamagePerSecond, float timePerDamage, Texture texture, List<Bullet> bullets) {
        super(duration, texture);
        this.healthDamagePerSecond = healthDamagePerSecond;
        this.timePerDamage = timePerDamage;
        this.bullets = bullets;
    }

    public Poison(float duration, int healthDamagePerSecond, Texture texture) {
        super(duration, texture);
        this.healthDamagePerSecond = healthDamagePerSecond;
        this.timePerDamage = 1f;
    }

    public Poison(float duration) {
        super(duration, new Texture(Gdx.files.internal("poison.png")));
        this.healthDamagePerSecond = 1;
        this.timePerDamage = 1f;
    }

    @Override
    protected void apply(Hero hero, float deltaTime) {
        if (!applied) {
            hero.takeDamage(healthDamagePerSecond, armorIgnore);
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
            hero.takeDamage(healthDamagePerSecond, armorIgnore);
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

    private int getDamage(){ return healthDamagePerSecond;}
}
