package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;

import java.util.List;

public class LongAttackEnemy extends Enemy {
    private Texture bulletTexture;
    private List<Bullet> bullets;

    public LongAttackEnemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, float attackCooldown, Texture bulletTexture, List<Bullet> bullets, GameMap gameMap, LongAttackAI ai) {
        super(texture, x, y, width, height, speed, health, damage, bullets, ai, gameMap);
        setAttackCooldown(attackCooldown);
        this.bulletTexture = bulletTexture;
        this.bullets = bullets;
    }

    @Override
    public void attack(Hero hero) {
        float angle = (float) Math.toDegrees(Math.atan2(hero.getCenterY() - getCenterY(), hero.getCenterX() - getCenterX()));
        String animationPath;
        switch(getDamage()){
            case 1:
                animationPath = "core/assets/-1.png";
                break;
            case 2:
                animationPath = "core/assets/-2.png";
                break;
            case 3:
                animationPath = "core/assets/-3.png";
                break;
            case 4:
                animationPath = "core/assets/-4.png";
                break;
            case 5:
                animationPath = "core/assets/-5.png";
                break;
            default:
                animationPath = "core/assets/sparkle.png";
        }
        bullets.add(new Bullet(getCenterX(), getCenterY(), angle, bulletTexture, animationPath, true, this, 30, 10,450f));
        resetAttackCooldown();
    }
}
