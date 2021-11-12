package com.molean.isletopia.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class StatsSerializeUtils {
    private static final String TO_JSON_METHOD = "b";
    private static final String STATS_FIELD = "a";
    private static final String LOAD_STATS_METHOD = "a";
    private static Method getHandleMethod;
    private static Method getStatisticManagerMethod;
    private static Method toJsonMethod;
    private static Method getMinecraftServerMethod;
    private static Field statsField;
    private static Method loadStatsMethod;
    private static Method getDataFixerMethod;

    static {
        try {
            Class<?> craftPlayerClass = NMSUtils.getBukkitClass("entity.CraftPlayer");
            Class<?> entityPlayerClass = NMSUtils.getNMSClass("server.level.EntityPlayer");
            Class<?> serverStatisticManagerClass = NMSUtils.getNMSClass("stats.ServerStatisticManager");
            Class<?> statisticManagerClass = NMSUtils.getNMSClass("stats.StatisticManager");
            Class<?> dataFixerClass = Class.forName("com.mojang.datafixers.DataFixer");
            Class<?> minecraftServerClass = NMSUtils.getNMSClass("server.MinecraftServer");
            Class<?> entityClass = NMSUtils.getNMSClass("world.entity.Entity");
            getHandleMethod = craftPlayerClass.getDeclaredMethod("getHandle");
            getHandleMethod.setAccessible(true);
            getStatisticManagerMethod = entityPlayerClass.getDeclaredMethod("getStatisticManager");
            getStatisticManagerMethod.setAccessible(true);
            toJsonMethod = serverStatisticManagerClass.getDeclaredMethod(TO_JSON_METHOD);
            toJsonMethod.setAccessible(true);
            getMinecraftServerMethod = entityClass.getDeclaredMethod("getMinecraftServer");
            getMinecraftServerMethod.setAccessible(true);
            statsField = statisticManagerClass.getDeclaredField(STATS_FIELD);
            statsField.setAccessible(true);
            loadStatsMethod = serverStatisticManagerClass.getDeclaredMethod(LOAD_STATS_METHOD, dataFixerClass, String.class);
            loadStatsMethod.setAccessible(true);
            getDataFixerMethod = minecraftServerClass.getDeclaredMethod("getDataFixer");
            getDataFixerMethod.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStats(Player player) {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Must run in main thread");
        }
        try {
            Object entityPlayer = getHandleMethod.invoke(player);
            Object serverStatisticManager = getStatisticManagerMethod.invoke(entityPlayer);
            toJsonMethod.setAccessible(true);
            return (String) toJsonMethod.invoke(serverStatisticManager);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't get stats by nms!");
        }
    }

    public static void loadStats(Player player, String json) {
        if (!Bukkit.isPrimaryThread()) {
            throw new RuntimeException("Must run in main thread");
        }
        try {
            Object entityPlayer = getHandleMethod.invoke(player);
            Object minecraftServer = getMinecraftServerMethod.invoke(entityPlayer);
            Object statisticManager = getStatisticManagerMethod.invoke(entityPlayer);
            statsField.setAccessible(true);
            Map<?, ?> o = (Map<?, ?>) statsField.get(statisticManager);
            o.clear();
            Object dataFixer = getDataFixerMethod.invoke(minecraftServer);
            loadStatsMethod.invoke(statisticManager, dataFixer, json);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't load stats by nms!");
        }
    }
}
