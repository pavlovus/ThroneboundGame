package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.darkknight.Hero; // Assuming Hero class exists

public class TeleportAttackEffect {
    private static final String EXPLOSION_SHEET_PATH = "core/assets/teleport_attack.png";
    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 32;
    private static final float FRAME_DURATION = 0.035f;
    private static final int TOTAL_FRAMES = 10;
    private static Texture explosionSheet;
    private static Animation<TextureRegion> explosionAnimation;

    // Static initialization block
    static {
        explosionSheet = new Texture(EXPLOSION_SHEET_PATH);
        TextureRegion[][] tmp = TextureRegion.split(explosionSheet, FRAME_WIDTH, FRAME_HEIGHT);

        Array<TextureRegion> animationFrames = new Array<>();
        int count = 0;

        for (int i = 0; i < tmp.length && count < TOTAL_FRAMES; i++) {
            for (int j = 0; j < tmp[i].length && count < TOTAL_FRAMES; j++) {
                animationFrames.add(tmp[i][j]);
                count++;
            }
        }

        explosionAnimation = new Animation<>(FRAME_DURATION, animationFrames);
        explosionAnimation.setPlayMode(Animation.PlayMode.NORMAL);
    }

    private float stateTime;
    private float offsetX, offsetY;
    private float width, height;
    private boolean finished;
    private Hero targetHero; // Reference to the hero to follow

    // Modified constructor to accept Hero instead of just position
    public TeleportAttackEffect(Hero targetHero, float width, float height) {
        this.targetHero = targetHero;
        this.width = width;
        this.height = height;
        this.stateTime = 0f;
        this.finished = false;

        // Calculate initial offsets to center the effect relative to the hero
        this.offsetX = (targetHero.getWidth() / 2f) - (width / 2f);
        this.offsetY = (targetHero.getHeight() / 2f) - (height / 2f);
    }

    public void update(float delta) {
        stateTime += delta;

        // Check if animation is finished or hero is null
        if (explosionAnimation.isAnimationFinished(stateTime) || targetHero == null) {
            finished = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!finished && targetHero != null) {
            TextureRegion currentFrame = explosionAnimation.getKeyFrame(stateTime);
            // Calculate draw position based on hero's current position and offsets
            float drawX = targetHero.getX() + offsetX;
            float drawY = targetHero.getY() + offsetY;
            batch.draw(currentFrame, drawX, drawY, width, height);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public static void disposeStaticAssets() {
        if (explosionSheet != null) {
            explosionSheet.dispose();
            explosionSheet = null;
        }
    }
}
