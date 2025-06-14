package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;

public class Assets {
    public static Texture ghost_2Texture;
    public static Texture ghost_3Texture;
    public static Texture healer_2Texture;
    public static Texture healer_3Texture;
    public static Texture long_1Texture;
    public static Texture long_2Texture;
    public static Texture long_3Texture;
    public static Texture mom_1Texture;
    public static Texture mom_2Texture;
    public static Texture mom_3Texture;
    public static Texture short_1Texture;
    public static Texture short_2Texture;
    public static Texture short_3Texture;
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
    public static Texture healerEnemyTexture;
    public static Texture healingEffectTexture; // НОВА ТЕКСТУРА ДЛЯ ЕФЕКТУ ЗЦІЛЕННЯ

    public static Texture matriarchTexture;

    public static Texture poisonEffectTexture;

    public static Texture butcherTexture;
    public static Texture cleaverTexture;

    public static void load() {
        ghost_2Texture = new Texture("core/assets/ghost_2.png");
        ghost_3Texture = new Texture("core/assets/ghost_3.png");
        healer_2Texture = new Texture("core/assets/healer_2.png");
        healer_3Texture = new Texture("core/assets/healer_3.png");
        long_1Texture = new Texture("core/assets/long_1.png");
        long_2Texture = new Texture("core/assets/long_2.png");
        long_3Texture = new Texture("core/assets/long_3.png");
        mom_1Texture = new Texture("core/assets/mom_1.png");
        mom_2Texture = new Texture("core/assets/mom_2.png");
        mom_3Texture = new Texture("core/assets/mom_3.png");
        short_1Texture = new Texture("core/assets/short_1.png");
        short_2Texture = new Texture("core/assets/short_2.png");
        short_3Texture = new Texture("core/assets/short_3.png");
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

        butcherTexture = new Texture("core/assets/boss_1.png"); // Ваша текстура м'ясника
        cleaverTexture = new Texture("core/assets/boss_1_weapon.png"); // Ваша текстура тесака


        enemyBulletTexture = new Texture("core/assets/arrow.png");
        turretBulletTexture = new Texture("core/assets/turret_bullet.png");

        poisonEffectTexture = new Texture("assets/poison.png");

        healerEnemyTexture = new Texture("core/assets/healer.png");
        healingEffectTexture = new Texture("core/assets/healing_effect.png"); // ЗАВАНТАЖЕННЯ НОВОЇ ТЕКСТУРИ
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
        if (butcherTexture != null) butcherTexture.dispose();
        if (cleaverTexture != null) cleaverTexture.dispose();

        enemyBulletTexture.dispose();
        turretBulletTexture.dispose();

        poisonEffectTexture.dispose();
        healerEnemyTexture.dispose();
        healingEffectTexture.dispose();
    }
}
