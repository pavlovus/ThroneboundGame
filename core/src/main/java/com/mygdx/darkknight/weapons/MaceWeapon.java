package com.mygdx.darkknight.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.Hero;
import com.mygdx.darkknight.enemies.Enemy;

import java.util.ArrayList;
import java.util.List;

public class MaceWeapon extends Weapon {
    private boolean attacking = false;
    private boolean damageApplied = false;
    private boolean flip;
    private float attackTime = 0;
    private final float attackDuration = 0.2f;
    private float startAngle, targetAngle;
    private List<Enemy> enemies;
    private Hero hero;
    private Music sound;

    private enum AttackPhase { FORWARD, RETURN }
    private AttackPhase attackPhase = null;

    private Polygon bounds;
    private Texture animationTexture;
    private Animation<TextureRegion> hitAnimation;
    private float animationTime = 0f;
    private Vector2 hitPosition;
    private Vector2[] hitPositionsArray;
    private float hitSize;
    private boolean hit = false;
    private Vector2 position;
    private final List<Enemy> damagedEnemies = new ArrayList<>();

    public MaceWeapon(String texturePath, int damage, int width, int height, String animationPath, float hitSize) {
        super(texturePath, damage, width, height);
        this.animationTexture = new Texture(animationPath);
        this.hitSize = hitSize;
        this.position = new Vector2();
        sound = Gdx.audio.newMusic(Gdx.files.internal("mace.mp3"));
        initHitAnimation();
        this.setName("???");
    }

    public void draw(SpriteBatch batch, float centerX, float centerY, boolean flip) {
        batch.draw(
            getTexture(),
            flip ? centerX - getWidth()/4f : centerX + getWidth()/4f,
            flip ? centerY - getHeight() - getHeight()/3f : centerY - getHeight()/3f,
            0, flip ? getWidth() : 0,
            getWidth(), getHeight(),
            1, 1,
            getAngle(),
            0, 0,
            getTexture().getWidth(), getTexture().getHeight(),
            false, flip
        );
        this.flip = flip;
        position.x = flip ? centerX - getWidth()/4f : centerX + getWidth()/4f;
        position.y = flip ? centerY - getHeight() - getHeight()/3f : centerY - getHeight()/3f;

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

        if (hit && hitAnimation != null && hitPositionsArray != null) {
            TextureRegion currentFrame = hitAnimation.getKeyFrame(animationTime, false);

            for (Vector2 pos : hitPositionsArray) {
                batch.draw(currentFrame, pos.x, pos.y);
            }
        }
    }

    @Override
    public void update(float deltaTime, Hero hero) {
        if (hit) {
            animationTime += deltaTime;
            if (hitAnimation.isAnimationFinished(animationTime)) {
                hit = false;
            }
        }

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
                hit(enemies);
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
            sound.stop();
            sound.play();
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

    public void hit(List<Enemy> enemies) {
        hit = true;
        animationTime = 0f;

        float radius = 60f;
        float halfSize = hitSize / 2f;

        // Кути для півкола анімацій удару
        float[] anglesDeg;
        if (flip) {
            anglesDeg = new float[]{90, 120, 150, 180, 210, 240, 270};
        } else {
            anglesDeg = new float[]{270, 300, 330, 360, 30, 60, 90};
        }

        hitPositionsArray = new Vector2[anglesDeg.length];

        for (int i = 0; i < anglesDeg.length; i++) {
            float angleRad = (float) Math.toRadians(anglesDeg[i]);
            float x = position.x + (flip ? -getWidth()*3/2f : getWidth()/2f) + radius * (float) Math.cos(angleRad);
            float y = position.y + (flip ? getHeight() : 0) - radius * (float) Math.sin(angleRad);
            hitPositionsArray[i] = new Vector2(x, y);
        }

        // Обчислюємо bounding rectangle, що містить усі позиції анімації (з урахуванням розміру)
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (Vector2 pos : hitPositionsArray) {
            minX = Math.min(minX, pos.x - halfSize);
            minY = Math.min(minY, pos.y - halfSize);
            maxX = Math.max(maxX, pos.x + halfSize);
            maxY = Math.max(maxY, pos.y + halfSize);
        }

        Rectangle explosionArea = new Rectangle(minX, minY, maxX - minX, maxY - minY);

        // Перевіряємо зіткнення ворогів із цією зоною, але не завдаємо урон тим, хто вже його отримав
        for (Enemy enemy : enemies) {
            if (explosionArea.overlaps(enemy.getBoundingRectangle()) && !damagedEnemies.contains(enemy)) {
                enemy.takeDamage(getDamage());
                damagedEnemies.add(enemy); // Додаємо до списку уражених
            }
        }

        // Встановлюємо hitPosition у центр зони для малювання анімації
        hitPosition = new Vector2((minX + maxX) / 2f, (minY + maxY) / 2f);
    }

    private void initHitAnimation() {
        Texture sheet = animationTexture;
        TextureRegion[][] tmp = TextureRegion.split(sheet, 32, 32);
        TextureRegion[] frames = new TextureRegion[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            frames[i] = tmp[i][0];
        }
        hitAnimation = new Animation<>(0.05f, frames);
    }

    public boolean isAttacking() {
        return attacking;
    }
}




