package com.mygdx.darkknight.weapons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.MagicBullet;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.List;

public class WizardWeapon extends Weapon {
    private Texture bulletTexture;
    private float cooldownTime = 0;
    private final float cooldownDuration = 0.7f;

    public WizardWeapon(String texturePath, int damage, int width, int height, String bulletTexturePath) {
        super(texturePath, damage, width, height);
        bulletTexture = new Texture(bulletTexturePath);
        this.setName("Staff of wisdom");
    }

    public void draw(SpriteBatch batch, float centerX, float centerY, boolean flip) {
        batch.draw(
            getTexture(), flip ? centerX - getWidth()*7/8f  : centerX - getWidth()/8f, centerY - getHeight() /2f,
            getWidth() / 2f, getHeight() / 2f,
            getWidth(), getHeight(),
            1, 1,
            45f,
            0, 0,
            getTexture().getWidth(), getTexture().getHeight(),
            false, false
        );
    }

    public void update(float deltaTime, Hero hero){
        if (cooldownTime > 0)  cooldownTime -= deltaTime;
    }

    public void attack(Hero hero, List<Bullet> bullets, List<Enemy> enemies) {
        if (cooldownTime <= 0) {
            float weaponAngle = getAngle();
            float gunX = hero.getCenterX() + getWidth()/2;
            float gunY = hero.getCenterY() + getHeight() /2;
            bullets.add(new MagicBullet(gunX, gunY, weaponAngle + 7.5f, bulletTexture, "core/assets/sparkle.png", false, 8, 8, 600f, 0.5f,32f, this));
            bullets.add(new MagicBullet(gunX, gunY, weaponAngle - 7.5f, bulletTexture, "core/assets/sparkle.png", false, 8, 8, 600f, 0.5f,32f,this));
            bullets.add(new MagicBullet(gunX, gunY, weaponAngle + 22.5f, bulletTexture, "core/assets/sparkle.png", false, 8, 8, 600f, 0.5f,32f, this));
            bullets.add(new MagicBullet(gunX, gunY, weaponAngle - 22.5f, bulletTexture, "core/assets/sparkle.png", false, 8, 8, 600f, 0.5f,32f, this));
            cooldownTime = cooldownDuration;
        }
    }
}
