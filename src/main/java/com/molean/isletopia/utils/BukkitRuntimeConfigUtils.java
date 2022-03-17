package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.bukkit.World;
import org.bukkit.entity.SpawnCategory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BukkitRuntimeConfigUtils {
    public static void setWorld(SpawnCategory key, int value) {
        try {
            World world = IsletopiaTweakers.getWorld();
            Class<?> craftServer = NMSUtils.getBukkitClass("CraftWorld");
            Method getHandle = craftServer.getDeclaredMethod("getHandle");
            getHandle.setAccessible(true);
            Object worldServer = getHandle.invoke(world);
            Class<?> worldClass = NMSUtils.getNMSClass("world.level.World");
            Field declaredField = worldClass.getDeclaredField("ticksPerSpawnCategory");
            declaredField.setAccessible(true);
            Object2LongOpenHashMap<SpawnCategory> ticksPerSpawnCategory = (Object2LongOpenHashMap<SpawnCategory>) declaredField.get(worldServer);
            ticksPerSpawnCategory.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
