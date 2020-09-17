package com.molean.isletopia.story;


import com.molean.isletopia.story.story.Story;
import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class StoryManager {
    private static final Map<String, Story> map = new HashMap<>();

    public static void reload() {
        map.clear();
        File dataFolder = IsletopiaTweakers.getPlugin().getDataFolder();
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith("yml"));
        assert files != null;
        for (File file : files) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            Set<String> keys = yamlConfiguration.getKeys(false);
            for (String key : keys) {
                Story story = Story.parse(key, Objects.requireNonNull(yamlConfiguration.getConfigurationSection(key)));
                map.put(story.getId(), story);
            }
        }

    }

    public static ArrayList<String> getStoryIDs() {
        return new ArrayList<>(map.keySet());
    }

    public static Story getStory(String id) {
        return map.get(id);
    }


}
