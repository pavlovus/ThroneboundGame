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
import com.badlogic.gdx.utils.Array;


public class GameMap {
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private MapObjects obstacles;
    private MapObjects doors;
    private boolean doorsClosed = false;

    public GameMap(String mapFilePath) {
        try {
            map = new TmxMapLoader().load(mapFilePath);
            renderer = new OrthogonalTiledMapRenderer(map);
            obstacles = map.getLayers().get("Obstacles").getObjects();
            if (map.getLayers().get("Doors") != null)
                doors = map.getLayers().get("Doors").getObjects();

            // Вимикаємо графічний шар дверей на старті
            if (map.getLayers().get("DoorsModels") != null)
                map.getLayers().get("DoorsModels").setVisible(false);

        } catch (Exception e) {
            System.err.println("Не вдалося завантажити карту: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void render(OrthographicCamera camera) {
        renderer.setView(camera);

        // Збираємо всі індекси видимих tile-шарів
        Array<Integer> visibleLayers = new Array<>();
        for (int i = 0; i < map.getLayers().getCount(); i++) {
            if (map.getLayers().get(i).isVisible()) {
                visibleLayers.add(i);
            }
        }

        // Перетворюємо на int[]
        int[] layerIndices = new int[visibleLayers.size];
        for (int i = 0; i < visibleLayers.size; i++) {
            layerIndices[i] = visibleLayers.get(i);
        }

        renderer.render(layerIndices);
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

        if (doorsClosed && doors != null) {
            for (MapObject obj : doors) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle door = ((RectangleMapObject) obj).getRectangle();
                    if (rect.overlaps(door)) return true;
                }
            }
        }

        return false;
    }

    public boolean isTouchingDoors(Rectangle rect) {
        if (doors != null) {
            for (MapObject obj : doors) {
                if (obj instanceof RectangleMapObject) {
                    Rectangle door = ((RectangleMapObject) obj).getRectangle();
                    if (rect.overlaps(door)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void closeDoors() {
        doorsClosed = true;

        if (map.getLayers().get("DoorsModels") != null) {
            map.getLayers().get("DoorsModels").setVisible(true);
        }
    }

    public void openDoors() {
        doorsClosed = false;

        if (map.getLayers().get("DoorsModels") != null) {
            map.getLayers().get("DoorsModels").setVisible(false);
        }
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

    public boolean hasLineOfSight(Vector2 start, Vector2 end) {
        float dx = end.x - start.x;
        float dy = end.y - start.y;
        float distance = Vector2.dst(start.x, start.y, end.x, end.y);
        float steps = distance / 8f; // Check every 8 pixels

        for (float i = 0; i <= 1; i += 1f/steps) {
            float x = start.x + dx * i;
            float y = start.y + dy * i;
            if (isCellBlocked(new Rectangle(x, y, 1, 1))) {
                return false;
            }
        }
        return true;
    }

    public TiledMap getTiledMap() {
        return map;
    }

    public void dispose() {
        map.dispose();
        renderer.dispose();
    }

    public boolean isDoorsClosed() {
        return doorsClosed;
    }
}
