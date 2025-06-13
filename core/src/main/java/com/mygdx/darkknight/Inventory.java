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

    public void showChest(SpriteBatch batch, Chest chest) {
        chest.setTexture(map.getTiledMap().getTileSets().getTile(1).getTextureRegion().getTexture());
        if (chest.isVisible()) {
            batch.begin();
            batch.draw(chest.getTexture(), chest.getX() * 32, (chest.getY() - 1) * 32, 32, 32);
            batch.end();
        }
    }

    public void openChest(Chest chest) {
        chest.setOpened(true);
        if (!openedChests.contains(chest)) {
            openedChests.add(chest);
        }
        chest.setTexture(map.getTiledMap().getTileSets().getTile(1).getTextureRegion().getTexture());
    }

    public void hideChest(Chest chest) {
        chest.setVisible(false);
    }



}
