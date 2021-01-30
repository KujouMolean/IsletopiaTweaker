package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.Objects;

public class AutoShutDown {
    private static Long l;

    public AutoShutDown() {
        Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            l = System.currentTimeMillis();
        }, 0, 20);

        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            if (System.currentTimeMillis() - l > 1000 * 60) {
                new Thread(() -> {
                    try {
                        Thread.sleep(30 * 1000);
                        Class<?> aClass = Class.forName("java.lang.Shutdown");
                        Method halt = aClass.getDeclaredMethod("halt", int.class);
                        halt.setAccessible(true);
                        halt.invoke(null, -1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

                Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).save();
                Bukkit.shutdown();
            }
        }, 10, 100);
    }
}
