package com.mygdx.darkknight.weapons;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.List;

public class SwordWeapon extends Weapon {
    private float radius;
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

    public SwordWeapon(String texturePath, int damage, int width, int height, float radius) {
        super(texturePath, damage, width, height);
        this.radius = radius;
        this.setName("Sword of Insight");
    }

    public void draw(SpriteBatch batch, float centerX, float centerY, boolean flip) {
        batch.draw(
            getTexture(),
            flip ? centerX - getWidth()/4 : centerX + getWidth()/4, flip ? centerY - getHeight() - getHeight()/3 : centerY - getHeight()/3,
            0, flip ? getWidth() : 0,
            getWidth(), getHeight(),
            1, 1,
            getAngle(),
            0, 0,
            getTexture().getWidth(), getTexture().getHeight(),
            false, flip
        );
        this.flip = flip;
    }

    @Override
    public void update(float deltaTime, Hero hero) {
        if (!attacking) return;

        attackTime += deltaTime;
        float progress = Math.min(attackTime / attackDuration, 1f);
        float newAngle = startAngle + (targetAngle - startAngle) * progress;
        setAngle(newAngle);

        if (attackPhase == AttackPhase.FORWARD) {
            if (!damageApplied && progress >= 0.5f) {
                applyDamage();
                damageApplied = true;
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

            float direction = flip ? 1 : -1;
            startAngle = getAngle();
            targetAngle = startAngle + 90 * direction;
        }
    }

    private void applyDamage() {
        float attackStartAngle;
        float attackEndAngle;
        if (flip){
            attackStartAngle = startAngle;
            attackEndAngle = targetAngle;
        } else {
            attackStartAngle = startAngle + 180;
            attackEndAngle = targetAngle + 180;
        }
        for (Enemy enemy : enemies) {
            float ex = enemy.getX() - hero.getCenterX();
            float ey = enemy.getY() - hero.getCenterY();
            float distance = (float) Math.sqrt(ex * ex + ey * ey);
            float enemyAngle = (float) Math.toDegrees(Math.atan2(ey, ex));
            if (distance <= radius && withinSector(enemyAngle, attackStartAngle, attackEndAngle)) {
                enemy.takeDamage(getDamage());
            }
        }
    }

    private boolean withinSector(float angle, float startAngle, float endAngle) {
        angle = normalizeAngle(angle);
        startAngle = normalizeAngle(startAngle);
        endAngle = normalizeAngle(endAngle);

        if (startAngle < endAngle) {
            return angle >= startAngle && angle <= endAngle;
        } else {
            return angle >= startAngle || angle <= endAngle;
        }
    }

    private float normalizeAngle(float angle) {
        angle = angle % 360;
        if (angle < 0) angle += 360;
        return angle;
    }

    public boolean isAttacking(){return attacking;}

    public float getRadius(){return radius;}
}
