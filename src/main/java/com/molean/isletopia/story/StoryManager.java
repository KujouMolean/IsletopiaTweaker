package com.molean.isletopia.story;


import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.story.story.Story;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StoryManager {
    private static final Map<String, Story> map = new HashMap<>();

    public static void reload() {
        map.clear();
        String path = IsletopiaTweakers.getPlugin().getDataFolder().toPath().toString()+"/story/";
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
                Story story = Story.parse(key, configurationSection);
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
