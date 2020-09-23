package com.molean.isletopia.story.story;


import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class StoryManager {
    private static final Map<String, Map<String, Story>> map = new HashMap<>();

    public static void reload() {
        map.clear();
        String path = IsletopiaTweakers.getPlugin().getDataFolder().toPath().toString() + "/story/";
        File storyFolder = new File(path);
        File[] files = storyFolder.listFiles((dir, name) -> name.endsWith("yml"));
        assert files != null;
        for (File file : files) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            Set<String> keys = yamlConfiguration.getKeys(false);
            for (String key : keys) {
                ConfigurationSection configurationSection = yamlConfiguration.getConfigurationSection(key);
                if (configurationSection == null) {
                    continue;
                }
                Story story = Story.parse(configurationSection);
                String namespace = story.getNamespace();
                String name = story.getName();
                map.computeIfAbsent(namespace, k -> new HashMap<>());
                map.get(namespace).put(name, story);
            }
        }

    }

    public static List<String> getNamespaces() {
        return new ArrayList<>(map.keySet());
    }

    public static List<String> getNames(String namespace) {
        return new ArrayList<>(map.get(namespace).keySet());
    }

    public static Story getStory(String namespace, String name) {
        return map.get(namespace).get(name);
    }


}
