package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

public class GameMap {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMapTileLayer collisionLayer;

    public GameMap(String mapFilePath) {
        try {
            map = new TmxMapLoader().load(mapFilePath);
            renderer = new OrthogonalTiledMapRenderer(map);
            collisionLayer = (TiledMapTileLayer) map.getLayers().get("CollisionLayer");
        } catch (Exception e) {
            System.err.println("Не вдалося завантажити карту: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    public boolean isCellBlocked(float x, float y) {
        if (collisionLayer == null) {
            // Якщо шар колізій відсутній, вважай, що клітинка не заблокована
            return false;
        }
        int tileX = (int)(x / collisionLayer.getTileWidth());
        int tileY = (int)(y / collisionLayer.getTileHeight());

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
        if (cell == null || cell.getTile() == null) {
            return false;
        }

        return cell.getTile().getProperties().containsKey("blocked") &&
            cell.getTile().getProperties().get("blocked", Boolean.class);
    }

    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}
