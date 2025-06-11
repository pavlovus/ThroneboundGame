package com.mygdx.darkknight.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.List;

public class AxeWeapon extends Weapon {
    private float radius;
    private boolean attacking = false;
    private boolean damageApplied = false;
    private float attackTime = 0;
    private final float attackDuration = 1f; // у секундах
    private float startAngle, targetAngle;
    private List<Enemy> enemies;
    private Hero hero;
    private Polygon bounds;
    private float cooldownTime = 0;
    private final float cooldownDuration = 3f;

    public AxeWeapon(String texturePath, int damage, int width, int height, float radius) {
        super(texturePath, damage, width, height);
        this.radius = radius;
    }

    public void draw(SpriteBatch batch, float centerX, float centerY, boolean flip) {
        batch.draw(
            getTexture(),
            centerX, centerY - getHeight()/3,
            0, 0,
            getWidth(), getHeight(),
            1, 1,
            getAngle(),
            0, 0,
            getTexture().getWidth(), getTexture().getHeight(),
            false, false
        );
        float[] vertices = new float[] {
            0, 0,
            getWidth(), 0,
            getWidth(), getHeight(),
            0, getHeight()
        };
        bounds = new Polygon(vertices);
        bounds.setPosition(centerX, centerY - getHeight()/3);
        bounds.setOrigin(0, 0);
        bounds.setRotation(getAngle());
    }

    @Override
    public void update(float deltaTime, Hero hero) {
        if (cooldownTime > 0) {
            cooldownTime -= deltaTime;
        }

        if (!attacking || cooldownTime > 0) return;

        attackTime += deltaTime;
        float progress = Math.min(attackTime / attackDuration, 1f);
        float newAngle = startAngle + (targetAngle - startAngle) * progress;
        setAngle(newAngle);

        for (Enemy e : enemies) {
            if (bounds != null && Intersector.overlapConvexPolygons(bounds, e.getBoundingPolygon())) {
                e.takeDamage(getDamage());
            }
        }

        if (progress >= 1f) {
            attacking = false;
            damageApplied = false;
            cooldownTime = cooldownDuration;
        }
    }

    public void attack(Hero hero, List<Bullet> bullets, List<Enemy> enemies) {
        if (!isAttacking()){
            startAttack(hero, enemies);
        }
    }

    public void startAttack(Hero hero, List<Enemy> enemies) {
        this.attacking = true;
        this.attackTime = 0;
        this.enemies = enemies;
        this.hero = hero;
        this.startAngle = getAngle();
        this.targetAngle = getAngle() - 360;
    }

    public boolean isAttacking(){return attacking;}

    public float getRadius(){return radius;}

    public Polygon getBounds(){ return bounds;}
}
