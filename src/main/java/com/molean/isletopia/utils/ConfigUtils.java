package com.molean.isletopia.utils;

import com.google.common.io.ByteStreams;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ConfigUtils {
    private static final Map<String, YamlConfiguration> configs = new HashMap<>();
    private static Plugin plugin;

    public static void setupConfig(Plugin plugin) {
        ConfigUtils.plugin = plugin;
    }

    public static File getFile(String name) {
        return new File(plugin.getDataFolder(), name);
    }

    public static void configOuput(String name) {
        try {
            if (!Files.exists(plugin.getDataFolder().toPath())) {
                Files.createDirectories(plugin.getDataFolder().toPath());
            }
            if (!Files.exists(getFile(name).toPath())) {
                Files.createFile(getFile(name).toPath());
                ByteStreams.copy(plugin.getResource(name), new FileOutputStream(getFile(name)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static YamlConfiguration getConfig(String filename) {
        if (configs.containsKey(filename))
            return configs.get(filename);
        YamlConfiguration yamlConfiguration = new YamlConfiguration();
        try {
            getFile(filename).createNewFile();
            yamlConfiguration.load(getFile(filename));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        configs.put(filename, yamlConfiguration);
        return yamlConfiguration;
    }

    public static void saveConfig(String filename) {
        YamlConfiguration yamlConfiguration = configs.get(filename);
        try {
            yamlConfiguration.save(getFile(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadConfig(String filename) {
        configs.put(filename, YamlConfiguration.loadConfiguration(getFile(filename)));
    }
}
