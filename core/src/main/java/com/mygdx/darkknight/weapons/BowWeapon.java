package com.mygdx.darkknight.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.List;

public class BowWeapon extends Weapon {
    private Texture bulletTexture;
    private Music sound;

    public BowWeapon(String texturePath, int damage, int width, int height, String bulletTexturePath) {
        super(texturePath, damage, width, height);
        sound = Gdx.audio.newMusic(Gdx.files.internal("bow_shoot.mp3"));
        bulletTexture = new Texture(bulletTexturePath);
        this.setName("Starbeam Bow");
    }

    public void draw(SpriteBatch batch, float centerX, float centerY, boolean flip) {
        batch.draw(
            getTexture(),
            flip ? centerX - getWidth() : centerX, centerY - getHeight() /2f,
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
        sound.stop();
        sound.play();
        float weaponAngle = getAngle();
        float gunX = hero.getCenterX();
        float gunY = hero.getCenterY();
        bullets.add(new Bullet(gunX, gunY, weaponAngle, bulletTexture,false, 30, 20, 500f, this));
    }
}
