package com.molean.isletopia.story;

import com.molean.isletopia.database.SceneDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneManager {

    private static final Map<String, List<String>> scenes = new HashMap<>();

    public static void registerScene(String namespace, String name) {
        if (!scenes.containsKey(namespace)) {
            scenes.put(namespace, new ArrayList<>());
        }
        scenes.get(namespace).add(name);
    }

    public static boolean hasScene(String namespace, String name) {
        if (!scenes.containsKey(namespace)) {
            return false;
        }
        return scenes.get(namespace).contains(name);
    }

    public static boolean setScene(PlayerScene playerScene) {
        if (!hasScene(playerScene.getNamespace(), playerScene.getName()))
            return false;
        SceneDao.setScene(playerScene);
        return true;
    }

    public static PlayerScene getScene(String player, String namespace, String name) {
        return SceneDao.getScene(player, namespace, name);
    }
}
