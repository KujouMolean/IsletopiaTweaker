package com.molean.isletopia.utils;

import com.google.common.collect.Table;
import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.World;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PaperRuntimeConfigUtils {
    public static Table<String, String, Integer> getSensorTickRatesTable() {
        try {
            World world = IsletopiaTweakers.getWorld();
            Class<?> craftServer = NMSUtils.getBukkitClass("CraftWorld");
            Method getHandle = craftServer.getDeclaredMethod("getHandle");
            getHandle.setAccessible(true);
            Object worldServer = getHandle.invoke(world);
            Class<?> worldClass = NMSUtils.getNMSClass("world.level.World");
            Field declaredField = worldClass.getDeclaredField("paperConfig");
            declaredField.setAccessible(true);
            Object paperWorldConfig = declaredField.get(worldServer);
            Class<?> paperWorldConfigClass = Class.forName("com.destroystokyo.paper.PaperWorldConfig");
            Field sensorTickRates = paperWorldConfigClass.getDeclaredField("sensorTickRates");
            sensorTickRates.setAccessible(true);
            Table<String,String,Integer> table = (Table<String, String, Integer>) sensorTickRates.get(paperWorldConfig);
            return table;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Table<String, String, Integer> getBehaviorTickRatesTable() {
        try {
            World world = IsletopiaTweakers.getWorld();
            Class<?> craftServer = NMSUtils.getBukkitClass("CraftWorld");
            Method getHandle = craftServer.getDeclaredMethod("getHandle");
            getHandle.setAccessible(true);
            Object worldServer = getHandle.invoke(world);
            Class<?> worldClass = NMSUtils.getNMSClass("world.level.World");
            Field declaredField = worldClass.getDeclaredField("paperConfig");
            declaredField.setAccessible(true);
            Object paperWorldConfig = declaredField.get(worldServer);
            Class<?> paperWorldConfigClass = Class.forName("com.destroystokyo.paper.PaperWorldConfig");
            Field sensorTickRates = paperWorldConfigClass.getDeclaredField("behaviorTickRates");
            sensorTickRates.setAccessible(true);
            Table<String, String, Integer> table = (Table<String, String, Integer>) sensorTickRates.get(paperWorldConfig);
            return table;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
