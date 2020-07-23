package com.molean.isletopia.tweakers;

import com.google.common.io.ByteStreams;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigUtils {
    private static Map<String, YamlConfiguration> configs = new HashMap<>();
    private static Plugin plugin;
    private static boolean setup = false;




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

    public static <T> Map<String, T> load(String name, Class<T> clazz) {
        HashMap<String, T> map = new HashMap<>();
        YamlConfiguration config = configs.get(name);
        Set<String> keys = config.getKeys(false);
        for (String key : keys) {
            T t = null;
            try {
                Constructor<T> constructor = clazz.getConstructor();
                t = constructor.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ConfigurationSection configurationSection = config.getConfigurationSection(key);
            Set<String> innerKeys = configurationSection.getKeys(false);
            for (String innerKey : innerKeys) {
                Object o = config.get(key + "." + innerKey);
                Field field = null;
                try {
                    field = clazz.getField(innerKey);
                    field.set(t, o);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            map.put(key, t);
        }
        return map;
    }
}
