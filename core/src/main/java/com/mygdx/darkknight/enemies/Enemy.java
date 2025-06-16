package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.darkknight.Bullet;
import com.mygdx.darkknight.GameMap;
import com.mygdx.darkknight.Hero;
import com.badlogic.gdx.math.Polygon;

import java.util.ArrayList;
import java.util.List;

public abstract class Enemy {
    protected Texture texture;
    float x;
    float y;
    private int width, height;
    private float speed;
    private int health;
    private int damage;
    private boolean dead;
    protected boolean armorIgnore;
    protected List<Bullet> bullets;
    protected EnemyAI ai;
    protected GameMap gameMap;
    private float attackCooldown = 0;
    private float attackTimer = 0;
    private BitmapFont damageFont;
    private List<DamageIndicator> damageIndicators = new ArrayList<>();
    private boolean nextIsRight = true;

    private static class DamageIndicator {
        String text;
        float timer;
        float yOffset;
        float alpha;
        boolean isRight;
        int damage;

        DamageIndicator(String text, boolean isRight, int dmg) {
            this.text = text;
            this.timer = 1.0f;
            this.yOffset = 0;
            this.alpha = 1.0f;
            this.isRight = isRight;
            this.damage = dmg;
        }
    }

    public Enemy(Texture texture, float x, float y, int width, int height, float speed, int health, int damage, List<Bullet> bullets, EnemyAI ai, GameMap gameMap, boolean armorIgnore) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.health = health;
        this.damage = damage;
        this.ai = ai;
        this.gameMap = gameMap;
        this.bullets = bullets;
        this.armorIgnore = armorIgnore;
        damageFont = new BitmapFont(Gdx.files.internal("assets/medievalLightFontSmaller.fnt"));
        damageFont.getData().setScale(1.0f);
    }

    public abstract void attack(Hero hero);

    public void update(Hero hero, float delta) {
        if (ai != null) ai.update(this, hero, delta);
        attackTimer -= delta;
        for (int i = damageIndicators.size() - 1; i >= 0; i--) {
            DamageIndicator indicator = damageIndicators.get(i);
            indicator.timer -= delta;
            indicator.yOffset += 30 * delta;
            indicator.alpha = Math.max(0, indicator.timer / 1.0f);
            if (indicator.timer <= 0) {
                damageIndicators.remove(i);
            }
        }
    }

    public void draw(SpriteBatch batch) {
        GlyphLayout layout = new GlyphLayout();

        batch.draw(texture, x, y, width, height);
        for (DamageIndicator indicator : damageIndicators) {
            layout.setText(damageFont, indicator.text);
            damageFont.setColor(1.0f, 1, 1, indicator.alpha);
            float textX = indicator.isRight ? x + width : x - layout.width * 2;
            textX += 13;
            float textY = y + height + indicator.yOffset;
            damageFont.draw(batch, indicator.text, textX, textY);
        }
        damageFont.setColor(1f, 1f, 1f, 1f);
    }

    public void move(float dx, float dy) {
        Rectangle futureRect = new Rectangle(x + dx, y + dy, width, height);
        if (!gameMap.isCellBlocked(futureRect)) {
            x += dx;
            y += dy;
        }
    }

    public void setPosition(float x, float y) {
        Rectangle futureRect = new Rectangle(x, y, width, height);
        if (!gameMap.isCellBlocked(futureRect)) {
            this.x = x;
            this.y = y;
        }
    }

    public float getCenterX() {
        return x + width / 2f;
    }

    public float getCenterY() {
        return y + height / 2f;
    }

    public Vector2 getCenter() {
        return new Vector2(getCenterX(), getCenterY());
    }

    public float distanceTo(Hero hero) {
        return Vector2.dst(getCenterX(), getCenterY(), hero.getCenterX(), hero.getCenterY());
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0) {
            dead = true;
        }
        damageIndicators.add(new DamageIndicator("-" + dmg, nextIsRight, dmg));
        nextIsRight = !nextIsRight;
    }

    public boolean canAttack() {
        return attackTimer <= 0;
    }

    public void resetAttackCooldown() {
        attackTimer = attackCooldown;
    }

    public void shootAt(float targetX, float targetY) {
    }

    public Rectangle getBoundingRectangle() {
        return new Rectangle(x, y, width, height);
    }

    public Polygon getBoundingPolygon() {
        float[] vertices = new float[] {
            0, 0,
            getWidth(), 0,
            getWidth(), getHeight(),
            0, getHeight()
        };
        Polygon p = new Polygon(vertices);
        p.setPosition(x, y);
        return p;
    }

    public boolean isDead() {
        return dead;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public float getSpeed() {
        return speed;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }

    public void setAttackCooldown(float cooldown) {
        this.attackCooldown = cooldown;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void dispose() {
        texture.dispose();
        damageFont.dispose();
    }

    public void setX(float x){
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean getArmorIgnore(){ return armorIgnore;}
}
