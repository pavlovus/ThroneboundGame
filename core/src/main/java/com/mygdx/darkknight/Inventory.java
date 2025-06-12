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
            if (!chest.opened) continue;

            float chestX = chest.x;
            float chestY = chest.y;

            List<Weapon> weapons = chest.weapons;

            for (int i = 0; i < weapons.size(); i++) {
                Weapon weapon = weapons.get(i);

                float drawX;
                if (i == 0) {
                    drawX = (chestX - 1) * 32; // зліва
                } else {
                    drawX = (chestX + 1) * 32; // справа
                }

                float drawY = chestY * 32;

                Texture texture = weapon.getTexture();
                batch.draw(texture, drawX, drawY, weapon.getWidth(), weapon.getHeight());

            }
        }
    }

    public void showChest() {
        System.out.println("Showing chest");
        if (tiledMap.getLayers().get("Chests") != null)
            tiledMap.getLayers().get("Chests").setVisible(true);
    }

    public void openChest(/*Chest chest*/) {
        if (tiledMap.getLayers().get("Chests") != null)
            tiledMap.getLayers().get("Chests").setVisible(false);

        if (tiledMap.getLayers().get("OpenedChests") != null)
            tiledMap.getLayers().get("OpenedChests").setVisible(true);

//        chest.opened = true;
//        openedChests.add(chest);
    }

    public void hideChest(/*hest chest*/) {
        if (tiledMap.getLayers().get("OpenedChests") != null)
            tiledMap.getLayers().get("OpenedChests").setVisible(false);
        if (tiledMap.getLayers().get("Chests") != null)
            tiledMap.getLayers().get("Chests").setVisible(false);
//        openedChests.remove(chest);
    }



}
