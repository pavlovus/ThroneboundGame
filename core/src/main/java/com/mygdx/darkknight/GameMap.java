package com.mygdx.darkknight;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


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

    public boolean isCellBlocked(Rectangle rect) {
        if (obstacles == null) return false;

        for (MapObject obj : obstacles) {
            if (obj instanceof RectangleMapObject) {
                Rectangle obstacle = ((RectangleMapObject) obj).getRectangle();
                if (rect.overlaps(obstacle)) {
                    return true;
                }
            }
        }
        return false;
    }
    public Vector2 getHeroSpawn() {
        for (MapObject obj : map.getLayers().get("Spawn").getObjects()) {
            if ("Spawn".equals(obj.getName())) {
                float x = Float.parseFloat(obj.getProperties().get("x").toString());
                float y = Float.parseFloat(obj.getProperties().get("y").toString());
                return new Vector2(x, y);
            }
        }
        return new Vector2(0, 0); // якщо не знайдено
    }


    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}
