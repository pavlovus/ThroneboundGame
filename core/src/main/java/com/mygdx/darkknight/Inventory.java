package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mygdx.darkknight.weapons.Weapon;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private GameMap map;
    private TiledMap tiledMap;
    private List<Chest> openedChests = new ArrayList<>();

    public Inventory(GameMap gameMap) {
        this.map = gameMap;
        this.tiledMap = gameMap.getTiledMap();
    }

    public void renderWeapons(SpriteBatch batch) {
        for (Chest chest : openedChests) {
            if (!chest.isOpened()) continue;

            float chestX = chest.getX();
            float chestY = chest.getY();

            Weapon weapon = chest.getWeapon();

            if (weapon != null) {
                float drawX = chestX * 32;
                float drawY = (chestY - 2) * 32;

                batch.begin();
                Texture texture = weapon.getTexture();
                batch.draw(texture, drawX, drawY, weapon.getWidth(), weapon.getHeight());
                batch.end();
            }
        }
    }

     boolean isPlayerNearChest(Hero hero, Chest chest) {
        float chestPixelX = chest.getX() * 32;
        float chestPixelY = chest.getY() * 32;

        float dx = hero.getX() - chestPixelX;
        float dy = hero.getY() - chestPixelY;

        float distanceSquared = dx * dx + dy * dy;
        return distanceSquared < 40 * 40;
    }

    boolean isPlayerNearWeapon(Hero hero, Chest chest) {
        float chestPixelX = chest.getX() * 32;
        float chestPixelY = (chest.getY() - 2) * 32;

        float dx = hero.getX() - chestPixelX;
        float dy = hero.getY() - chestPixelY;

        float distanceSquared = dx * dx + dy * dy;
        return distanceSquared < 40 * 40;
    }

    public void showChest(SpriteBatch batch, List<Chest> chests) {
        for (Chest chest : chests) {
            if (chest.isOpened()) {
                chest.setTexture("core/assets/chestOpened.png");
            } else {
                chest.setTexture("core/assets/chestClosed.png");
            }
        }
    }

    public void openChest(Chest chest) {
        chest.setOpened(true);
        if (!openedChests.contains(chest)) {
            openedChests.add(chest);
        }
    }


}
