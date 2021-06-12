package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.menu.settings.biome.LocalBiome;
import org.bukkit.Material;

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


public class MessageUtils {

    public MessageUtils() {
        List<String> resources = new ArrayList<>();
        resources.add("biome.properties");
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

//    public static String getMessage(String key) {
//        try {
//            Properties message = new Properties();
//
//            message.load(new FileInputStream(IsletopiaTweakers.getPlugin().getDataFolder() + "/message.properties"));
//            return message.getProperty(key);
//        } catch (IOException e) {
//            return key;
//        }
//    }

    public static LocalBiome getBiome(String biome) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(IsletopiaTweakers.getPlugin().getDataFolder() + "/biome.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String name;
        Material icon;

        String loadName = properties.getProperty(biome + ".name");

        name = Objects.requireNonNullElseGet(loadName, () -> biome + "(未知)");

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

    public static String getLocalServerName() {
        switch (ServerInfoUpdater.getServerName()){
            case "server1":
                return "马尔代夫";
            case "server2":
                return "东沙群岛";
            case "server3":
                return "西沙群岛";
            case "server4":
                return "南沙群岛";
            case "server5":
                return "夏威夷";
            case "server6":
                return "钓鱼群岛";
            case "server7":
                return "格陵兰岛";
            case "server8":
                return "不列颠群岛";
        }
        return "未知";
    }

}
