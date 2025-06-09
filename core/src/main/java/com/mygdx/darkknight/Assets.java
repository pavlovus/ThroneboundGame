package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;

// TODO Клас текстур, який дозволить кешувати усі текстурки, щоб гра не морозилась
public class Assets {
    public static Texture shortEnemyTexture;
    public static Texture longEnemyTexture;
    public static Texture ghostEnemyTexture;
    public static Texture bulletTexture;

    public static void load() {
        shortEnemyTexture = new Texture("core/assets/short1.png");
        longEnemyTexture = new Texture("core/assets/long1.png");
        ghostEnemyTexture = new Texture("core/assets/spec1.png");
        bulletTexture = new Texture("core/assets/bullet.png");
    }

    public static void dispose() {
        shortEnemyTexture.dispose();
        longEnemyTexture.dispose();
        ghostEnemyTexture.dispose();
        bulletTexture.dispose();
    }
}
