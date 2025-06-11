package com.mygdx.darkknight.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.List;

public class ArrowWeapon extends Weapon {
    private Texture bulletTexture;

    public ArrowWeapon(String texturePath, int damage, int width, int height, String bulletTexturePath) {
        super(texturePath, damage, width, height);
        bulletTexture = new Texture(bulletTexturePath);
    }

    public void draw(SpriteBatch batch, float centerX, float centerY, boolean flip) {
        batch.draw(
            getTexture(),
            centerX, centerY - getHeight() /2,
            getWidth() / 2f, getHeight() / 2f,
            getWidth(), getHeight(),
            1, 1,
            getAngle(),
            0, 0,
            getTexture().getWidth(), getTexture().getHeight(),
            false, flip
        );
    }

    public void update(float deltaTime, Hero hero){}

    public void attack(Hero hero, List<Bullet> bullets, List<Enemy> enemies){
        float weaponAngle = getAngle();
        float gunX = hero.getCenterX();
        float gunY = hero.getCenterY();
        bullets.add(new Bullet(gunX, gunY, weaponAngle, bulletTexture, false, 30, 10, 600f));
    }
}
