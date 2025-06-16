package com.mygdx.darkknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ScreenFader {
    private float alpha = 0f;
    private boolean fadingIn = false;
    private boolean fadingOut = false;
    private float duration = 1f;
    private float time = 0f;

    public void startFadeIn(float duration) {
        this.duration = duration;
        this.time = 0f;
        fadingIn = true;
        fadingOut = false;
        alpha = 1f;
    }

    public void startFadeOut(float duration) {
        this.duration = duration;
        this.time = 0f;
        fadingOut = true;
        fadingIn = false;
        alpha = 1f;
    }

    public boolean isFading() {
        return fadingIn || fadingOut;
    }

    public float getAlpha() {
        return alpha;
    }

    public void update(float delta) {
        if (fadingIn) {
            time += delta;
            alpha = 1f - (time / duration);
            if (alpha <= 0f) {
                alpha = 0f;
                fadingIn = false;
            }
        } else if (fadingOut) {
            time += delta;
            alpha = time / duration;
            if (alpha >= 1f) {
                alpha = 1f;
                fadingOut = false;
            }
        }
    }

    public void render(ShapeRenderer renderer) {
        if (alpha > 0f) {
            Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(new Color(0, 0, 0, alpha));
            renderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.end();
            Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
        }
    }
}
