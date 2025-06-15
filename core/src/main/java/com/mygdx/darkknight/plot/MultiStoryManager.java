package com.mygdx.darkknight.plot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MultiStoryManager {
    private Map<String, StoryManager> managers = new HashMap<>();

    public MultiStoryManager(String jsonFilePath) {
        loadAllManagers(jsonFilePath);
    }

    private void loadAllManagers(String filePath) {
        Json json = new Json();
        FileHandle file = Gdx.files.internal(filePath);
        JsonValue root = json.fromJson(null, file);

        for (JsonValue entry : root) {
            String key = entry.name;
            ArrayList<Cutscene> scenes = new ArrayList<>();
            for (JsonValue sceneJson : entry.get("scenes")) {
                Cutscene scene = json.readValue(Cutscene.class, sceneJson);
                scenes.add(scene);
            }
            managers.put(key, new StoryManager(scenes));
        }
    }

    public StoryManager getManager(String name) {
        return managers.get(name);
    }
}
