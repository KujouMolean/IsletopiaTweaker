package com.molean.isletopia.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MSPTUtils {
    public static double get() {
        try {
            Class<?> minecraftServerClass = Class.forName("net.minecraft.server.MinecraftServer");
            Method getServerMethod = minecraftServerClass.getDeclaredMethod("getServer");
            Object minecraftServer = getServerMethod.invoke(null);
            Field tickTimes60sField = minecraftServerClass.getDeclaredField("tickTimes60s");
            Object tickTimes60s = tickTimes60sField.get(minecraftServer);
            Method getAverageMethod = tickTimes60s.getClass().getDeclaredMethod("getAverage");
            return (double) getAverageMethod.invoke(tickTimes60s);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
