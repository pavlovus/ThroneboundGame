package com.mygdx.darkknight.plot;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.darkknight.Hero;

public class PlotCharacter {
    private float x, y;
    private StoryManager scene;
    private boolean talked = false;
    private Texture texture;
    private boolean visible = false;


    public PlotCharacter(float x, float y, StoryManager scene, String texturePath) {
        this.x = x;
        this.y = 600 - y;
        this.scene = scene;
        this.texture = new Texture(texturePath);
    }

    public boolean isPlayerNearCharacter(Hero hero) {
        float characterPixelX = getX() * 32;
        float characterPixelY = getY() * 32;

        float dx = hero.getX() - characterPixelX;
        float dy = hero.getY() - characterPixelY;

        float distanceSquared = dx * dx + dy * dy;
        return distanceSquared < 65 * 65;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public StoryManager getScene() {
        return scene;
    }

    public boolean isTalked() {
        return talked;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setScene(StoryManager scene) {
        this.scene = scene;
    }

    public void setTexture(String texturePath) {
        texture = new Texture(texturePath);
        this.texture = texture;
    }

    public void setTalked(boolean talked) {
        this.talked = talked;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
