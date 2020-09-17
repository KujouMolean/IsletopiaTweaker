package com.molean.isletopia.story;

import com.molean.isletopia.parameter.UniversalParameter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private static final Map<String, Integer> scenes = new HashMap<>();

    public static void registScene(String namespace) {
        scenes.put(namespace, scenes.getOrDefault(namespace, 1));
    }

    public static boolean hasScene(String scene, int n) {
        return scenes.getOrDefault(scene, 1) <= n;
    }

    public static boolean setScene(String player, String namespace, int n, Location location) {
        if (!hasScene(namespace, n)) {
            return false;
        }
        String sceneSet = UniversalParameter.getParameter(player, "sceneSet");
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        if (sceneSet != null) {
            try {
                yamlConfiguration.loadFromString(sceneSet);
            } catch (InvalidConfigurationException e) {
                return false;
            }
        }
        ConfigurationSection section = yamlConfiguration.createSection(namespace + "_" + n);
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        UniversalParameter.setParameter(player, "sceneSet", yamlConfiguration.saveToString());
        return true;
    }

    public static Location getScene(String player, String namespace, int n) {
        String sceneSet = UniversalParameter.getParameter(player, "sceneSet");
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            yamlConfiguration.loadFromString(sceneSet);
        } catch (InvalidConfigurationException e) {
            return null;
        }
        ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(namespace + "_" + n);
        if (configurationSection == null) {
            return null;
        }
        Location location = new Location(null, 0, 0, 0);
        location.setX(configurationSection.getInt("x"));
        location.setY(configurationSection.getInt("y"));
        location.setZ(configurationSection.getInt("z"));
        return location;
    }
}
