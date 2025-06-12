package com.mygdx.darkknight.enemies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class TeleportAttackEffect {
    private static final String EXPLOSION_SHEET_PATH = "core/assets/teleport_attack.png";
    private static final int FRAME_WIDTH = 32;
    private static final int FRAME_HEIGHT = 32;
    private static final float FRAME_DURATION = 0.035f;
    private static final int TOTAL_FRAMES = 10;
    private static Texture explosionSheet;
    private static Animation<TextureRegion> explosionAnimation;

    // Статичний блок ініціалізації: виконується один раз при першому зверненні до класу
    static {
        explosionSheet = new Texture(EXPLOSION_SHEET_PATH);
        TextureRegion[][] tmp = TextureRegion.split(explosionSheet, FRAME_WIDTH, FRAME_HEIGHT);

        Array<TextureRegion> animationFrames = new Array<>();
        int count = 0;

        // Виправлено логіку зчитування кадрів - спочатка по рядках, потім по стовпцях
        for (int i = 0; i < tmp.length && count < TOTAL_FRAMES; i++) {
            for (int j = 0; j < tmp[i].length && count < TOTAL_FRAMES; j++) {
                animationFrames.add(tmp[i][j]);
                count++;
            }
        }

        explosionAnimation = new Animation<>(FRAME_DURATION, animationFrames);
        explosionAnimation.setPlayMode(Animation.PlayMode.NORMAL); // Програється один раз
    }

    private float stateTime;
    private float x, y;
    private float width, height;
    private boolean finished;

    // Конструктор тепер приймає лише позицію та розмір ефекту
    public TeleportAttackEffect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.stateTime = 0f;
        this.finished = false;
    }

    public void update(float delta) {
        stateTime += delta;
        if (explosionAnimation.isAnimationFinished(stateTime)) {
            finished = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!finished) {
            TextureRegion currentFrame = explosionAnimation.getKeyFrame(stateTime);
            // Центруємо ефект відносно позиції героя
            float drawX = x - (width - 32) / 2; // 32 - розмір одного кадру
            float drawY = y - (height - 32) / 2;
            batch.draw(currentFrame, drawX, drawY, width, height);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    // Метод для звільнення статичних ресурсів (викликається один раз при завершенні гри)
    public static void disposeStaticAssets() {
        if (explosionSheet != null) {
            explosionSheet.dispose();
            explosionSheet = null;
        }
    }
}
