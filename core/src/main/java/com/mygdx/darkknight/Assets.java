package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;

public class Assets {
    public static Texture shortEnemyTexture;
    public static Texture longEnemyTexture;
    public static Texture ghostEnemyTexture;
    public static Texture turretTopTexture;
    public static Texture turretBaseTexture;
    public static Texture teleporterTexture;
    public static Texture teleportEffectTexture;
    public static Texture meteorTexture;
    public static Texture meteorWarningTexture;
    public static Texture enemyBulletTexture;
    public static Texture turretBulletTexture;
    public static Texture meteorExplosionTexture;

    public static Texture matriarchTexture;

    public static Texture poisonEffectTexture;

    public static void load() {
        shortEnemyTexture = new Texture("core/assets/short1.png");
        longEnemyTexture = new Texture("core/assets/long1.png");
        ghostEnemyTexture = new Texture("core/assets/spec1.png");
        turretTopTexture = new Texture("core/assets/turret_top.png");
        turretBaseTexture = new Texture("core/assets/turret_base.png");
        teleporterTexture = new Texture("core/assets/teleporter.png");
        teleportEffectTexture = new Texture("core/assets/teleport_effect.png");
        meteorTexture = new Texture("core/assets/meteor.png");
        meteorWarningTexture = new Texture("core/assets/warning.png");
        meteorExplosionTexture = new Texture("core/assets/meteor_explosion.png");

        matriarchTexture = new Texture("core/assets/matriarch.png");

        enemyBulletTexture = new Texture("core/assets/arrow.png");
        turretBulletTexture = new Texture("core/assets/turret_bullet.png");

        poisonEffectTexture = new Texture("assets/poison.png");
    }

    public static void dispose() {
        shortEnemyTexture.dispose();
        longEnemyTexture.dispose();
        ghostEnemyTexture.dispose();
        turretTopTexture.dispose();
        turretBaseTexture.dispose();
        teleporterTexture.dispose();
        teleportEffectTexture.dispose();
        meteorTexture.dispose();
        meteorWarningTexture.dispose();
        meteorExplosionTexture.dispose();

        if (matriarchTexture != null) matriarchTexture.dispose();

        enemyBulletTexture.dispose();
        turretBulletTexture.dispose();

        poisonEffectTexture.dispose();
    }
}
