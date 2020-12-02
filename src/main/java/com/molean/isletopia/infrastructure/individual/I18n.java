package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.menu.settings.biome.LocalBiome;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


//Why not just use resource bundle ?
// because you can not edit the configuration in game.


public class I18n {

    public I18n() {
        List<String> resources = new ArrayList<>();
        resources.add("biome_en.properties");
        resources.add("biome_zh.properties");
        resources.add("message_zh.properties");
        resources.add("message_en.properties");
        for (String resource : resources) {
            InputStream inputStream = IsletopiaTweakers.getPlugin().getResource(resource);
            String file = IsletopiaTweakers.getPlugin().getDataFolder() + "/" + resource;
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                if (inputStream != null) {
                    outputStream.write(inputStream.readAllBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getMessage(String key, Player player) {
        try {
            Properties message = new Properties();

            if (player.getLocale().startsWith("zh")) {
                FileInputStream fileInputStream = new FileInputStream(IsletopiaTweakers.getPlugin().getDataFolder() + "/message_zh.properties");

                message.load(fileInputStream);
            } else {
                message.load(new FileInputStream(IsletopiaTweakers.getPlugin().getDataFolder() + "/message_en.properties"));
            }
            return message.getProperty(key);
        } catch (IOException e) {
            return key;
        }
    }

    public static LocalBiome getBiome(String biome, Player player) {
        Properties properties = new Properties();
        if (player.getLocale().startsWith("zh")) {
            try {
                properties.load(new FileInputStream(IsletopiaTweakers.getPlugin().getDataFolder() + "/biome_zh.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                properties.load(new FileInputStream(IsletopiaTweakers.getPlugin().getDataFolder() + "/biome_en.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String name = null;
        Material icon = null;

        String loadName = properties.getProperty(biome + ".name");

        name = Objects.requireNonNullElseGet(loadName, () -> biome + "(UNKNOWN)");

        String loadIcon = properties.getProperty(biome + ".icon");
        if (loadIcon != null) {
            icon = Material.valueOf(loadIcon);
        } else {
            icon = Material.PLAYER_HEAD;
        }

        List<String> creatures = new ArrayList<>();
        List<String> environment = new ArrayList<>();

        String loadCreatures = properties.getProperty(biome + ".creatures");
        if (loadCreatures != null) {
            creatures.addAll(List.of(loadCreatures.split(",")));
        }
        String loadEnvironment = properties.getProperty(biome + ".environment");
        if (loadEnvironment != null) {
            environment.addAll(List.of(loadEnvironment.split(",")));
        }

        return new LocalBiome(biome, name, icon, creatures, environment);
    }

    public static String getLocalServerName(Player player) {
        return I18n.getMessage(ServerInfoUpdater.getServerName(), player);
    }

}
