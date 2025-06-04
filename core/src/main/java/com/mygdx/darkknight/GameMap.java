package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;

public class GameMap {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private MapObjects obstacles;

    public GameMap(String mapFilePath) {
        try {
            map = new TmxMapLoader().load(mapFilePath);
            renderer = new OrthogonalTiledMapRenderer(map);
            obstacles = map.getLayers().get("Obstacles").getObjects();
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
        if (obstacles == null) return false;

        for (MapObject object : obstacles) {
            if (object instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();
                if (rect.contains(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}
