package com.molean.isletopia.statistics.vanilla;

import com.google.gson.Gson;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class Stats {
    private Map<String, Map<String, Integer>> stats;
    private int DataVersion;

    private static final Map<Statistic, String> customMapping = new HashMap<>();
    private static final Map<String, Statistic> customReverseMapping = new HashMap<>();
    private static final Map<Statistic, String> typeMapping = new HashMap<>();
    private static final Map<String, Statistic> typeReverseMapping = new HashMap<>();
    private static final Gson gson = new Gson();

    static {

        try {
            Properties properties = null;
            InputStream inputStream = Stats.class.getClassLoader().getResourceAsStream("CustomStatistics.properties");
            properties = new Properties();
            properties.load(inputStream);
            for (String key : properties.stringPropertyNames()) {
                customMapping.put(Statistic.valueOf(key), properties.getProperty(key));
                customReverseMapping.put(properties.getProperty(key), Statistic.valueOf(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            Properties properties = null;
            InputStream inputStream = Stats.class.getClassLoader().getResourceAsStream("StatisticsType.properties");
            properties = new Properties();
            properties.load(inputStream);
            for (String key : properties.stringPropertyNames()) {
                typeMapping.put(Statistic.valueOf(key), properties.getProperty(key));
                typeReverseMapping.put(properties.getProperty(key), Statistic.valueOf(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Stats() {


    }

    public void setStats(Map<String, Map<String, Integer>> stats) {
        this.stats = stats;
    }

    public void setDataVersion(int dataVersion) {
        this.DataVersion = dataVersion;
    }

    public Map<String, Map<String, Integer>> getStats() {
        return stats;
    }

    public int getDataVersion() {
        return DataVersion;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public void merge(Stats other) {
        for (String outerKey : other.getStats().keySet()) {
            Map<String, Integer> innerMap = other.getStats().get(outerKey);
            if (!stats.containsKey(outerKey)) {
                stats.put(outerKey, new HashMap<>());
            }
            Map<String, Integer> thisInnerMap = stats.get(outerKey);
            for (String innerKey : innerMap.keySet()) {
                thisInnerMap.put(innerKey, innerMap.get(innerKey) + thisInnerMap.getOrDefault(innerKey, 0));
            }
        }
    }

    public void apply(Player player) {
        for (String outerKey : stats.keySet()) {
            Map<String, Integer> innerMap = stats.get(outerKey);
            String shortOuterKey = outerKey.replaceAll("minecraft:", "");
            for (String innerKey : innerMap.keySet()) {
                String shortInnerKey = innerKey.replaceAll("minecraft:", "");
                apply(player, shortOuterKey, shortInnerKey, innerMap.get(innerKey));
            }
        }
    }

    @SuppressWarnings("deprecation")
    private static void apply(Player player, String outerKey, String innerKey, Integer value) {
        if (outerKey.equalsIgnoreCase("custom")) {
            Statistic statistic = customReverseMapping.get(innerKey);
            if (statistic != null)
                player.setStatistic(statistic, value);
        } else {
            Statistic statistic = typeReverseMapping.get(outerKey);
            if (statistic.getType().equals(Statistic.Type.ENTITY)) {
                player.setStatistic(statistic, Objects.requireNonNull(EntityType.fromName(innerKey)), value);
            } else {
                try {
                    player.setStatistic(statistic, Material.valueOf(innerKey.toUpperCase()), value);
                } catch (IllegalArgumentException ignore) {
                }
            }
        }
    }

    public static Stats fromJson(String string) {
        return gson.fromJson(string, Stats.class);
    }

    @SuppressWarnings("deprecation")
    public static Stats fromPlayer(Player player) {
        Stats stats = new Stats();
        stats.stats = new HashMap<>();
        stats.setDataVersion(2586);
        for (Statistic statistic : Statistic.values()) {
            if (typeMapping.containsKey(statistic)) {
                String s = typeMapping.get(statistic);
                if (!stats.stats.containsKey("minecraft:" + s)) {
                    stats.stats.put("minecraft:" + s, new HashMap<>());
                }
                if (statistic.getType().equals(Statistic.Type.ENTITY)) {
                    for (EntityType entityType : EntityType.values()) {
                        if (entityType.equals(EntityType.UNKNOWN)) {
                            continue;
                        }
                        int value = player.getStatistic(statistic, entityType);
                        if (value != 0) {
                            stats.stats.get("minecraft:" + s).put("minecraft:" + entityType.getName(), value);
                        }
                    }
                } else {
                    for (Material material : Material.values()) {
                        int value = player.getStatistic(statistic, material);
                        if (value != 0) {
                            stats.stats.get("minecraft:" + s).put("minecraft:" + material.name().toLowerCase(), value);
                        }
                    }
                }
            } else {
                if (!stats.stats.containsKey("minecraft:custom")) {
                    stats.stats.put("minecraft:custom", new HashMap<>());
                }
                int value = player.getStatistic(statistic);
                if (value != 0) {
                    stats.stats.get("minecraft:custom").put("minecraft:" + customMapping.get(statistic), value);
                }
            }
        }
        return stats;
    }
}
