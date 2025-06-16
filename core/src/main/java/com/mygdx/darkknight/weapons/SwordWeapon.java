package com.mygdx.darkknight.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.ArrayList;
import java.util.List;

public class SwordWeapon extends Weapon {
    private boolean attacking = false;
    private boolean damageApplied = false;
    private boolean flip;
    private float attackTime = 0;
    private final float attackDuration = 0.1f; // у секундах
    private float startAngle, targetAngle;
    private List<Enemy> enemies;
    private Hero hero;
    private enum AttackPhase { FORWARD, RETURN }
    private AttackPhase attackPhase = null;
    private Polygon bounds;
    private final List<Enemy> damagedEnemies = new ArrayList<>();

    public SwordWeapon(String texturePath, int damage, int width, int height) {
        super(texturePath, damage, width, height);
        this.setName("Sword of Insight");
    }

    public void draw(SpriteBatch batch, float centerX, float centerY, boolean flip) {
        batch.draw(
            getTexture(),
            flip ? centerX - getWidth()/4f : centerX + getWidth()/4f, flip ? centerY - getHeight() - getHeight()/3f : centerY - getHeight()/3f,
            0, flip ? getWidth() : 0,
            getWidth(), getHeight(),
            1, 1,
            getAngle(),
            0, 0,
            getTexture().getWidth(), getTexture().getHeight(),
            false, flip
        );
        this.flip = flip;

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
        if (!attacking) return;

        attackTime += deltaTime;
        float progress = Math.min(attackTime / attackDuration, 1f);
        float newAngle = startAngle + (targetAngle - startAngle) * progress;
        setAngle(newAngle);

        if (attackPhase == AttackPhase.FORWARD) {
            for (Enemy e : enemies) {
                if (bounds != null && Intersector.overlapConvexPolygons(bounds, e.getBoundingPolygon())) {
                    if (!damagedEnemies.contains(e)) {
                        e.takeDamage(getDamage());
                        damagedEnemies.add(e);
                    }
                }
            }

            if (progress >= 1f) {
                attackPhase = AttackPhase.RETURN;
                attackTime = 0;
                float forwardEnd = getAngle();
                float direction = flip ? 1 : -1;
                startAngle = forwardEnd;
                targetAngle = forwardEnd - 90 * direction;
            }
        } else if (attackPhase == AttackPhase.RETURN) {
            if (progress >= 1f) {
                attacking = false;
                attackPhase = null;
                damageApplied = false;
            }
        }
    }

    public void attack(Hero hero, List<Bullet> bullets, List<Enemy> enemies){
        if (!isAttacking()){
            startAttack(hero, enemies);
        }
    }

    public void startAttack(Hero hero, List<Enemy> enemies) {
        if (!attacking) {
            attacking = true;
            attackPhase = AttackPhase.FORWARD;
            attackTime = 0;
            this.enemies = enemies;
            this.hero = hero;
            damagedEnemies.clear();

            float direction = flip ? 1 : -1;
            startAngle = getAngle();
            targetAngle = startAngle + 90 * direction;
        }
    }

    public boolean isAttacking(){return attacking;}
}
