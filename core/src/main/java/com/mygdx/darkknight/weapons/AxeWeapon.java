package com.mygdx.darkknight.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.ArrayList;
import java.util.List;

public class AxeWeapon extends Weapon {
    private boolean attacking = false;
    private float attackTime = 0;
    private final float attackDuration = 0.5f; // у секундах
    private float startAngle;
    private float targetAngle;
    private List<Enemy> enemies;
    private Hero hero;
    private Polygon bounds;
    private float cooldownTime = 0;
    private final float cooldownDuration = 2f;
    private final List<Enemy> damagedEnemies = new ArrayList<>();

    private final float fixedStartAngle = 45f;

    public AxeWeapon(String texturePath, int damage, int width, int height) {
        super(texturePath, damage, width, height);
        setAngle(fixedStartAngle);
        this.setName("Axe of Divine Wrath");
    }

    public void draw(SpriteBatch batch, float centerX, float centerY, boolean flip) {
        batch.draw(
            getTexture(),
            centerX + getWidth()/3, centerY - getHeight() / 3f,
            0, 0,
            getWidth(), getHeight(),
            1, 1,
            attacking ? getAngle() : 45f,
            0, 0,
            getTexture().getWidth(), getTexture().getHeight(),
            false, false
        );

        float[] vertices = new float[]{
            0, 0,
            getWidth(), 0,
            getWidth(), getHeight(),
            0, getHeight()
        };
        bounds = new Polygon(vertices);
        bounds.setPosition(centerX, centerY - getHeight() / 3f);
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
                if (!damagedEnemies.contains(e)) {
                    e.takeDamage(getDamage());
                    damagedEnemies.add(e);
                }
            }
        }

        if (progress >= 1f) {
            attacking = false;
            cooldownTime = cooldownDuration;
            setAngle(fixedStartAngle); // повернути до стартового кута після атаки
        }
    }

    public void attack(Hero hero, List<Bullet> bullets, List<Enemy> enemies) {
        if (!isAttacking()) {
            startAttack(hero, enemies);
        }
    }

    public void startAttack(Hero hero, List<Enemy> enemies) {
        this.attacking = true;
        this.attackTime = 0;
        this.enemies = enemies;
        this.hero = hero;
        damagedEnemies.clear();
        this.startAngle = fixedStartAngle;
        this.targetAngle = fixedStartAngle - 360f;
        setAngle(fixedStartAngle);
    }

    public boolean isAttacking() {
        return attacking;
    }

    public Polygon getBounds() {
        return bounds;
    }
}
