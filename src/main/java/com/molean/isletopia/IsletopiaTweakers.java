package com.molean.isletopia;

import com.molean.isletopia.admin.IsletopiaAdmin;
import com.molean.isletopia.bungee.IsletopiaBungee;
import com.molean.isletopia.distribute.IsletopiaDistributeSystem;
import com.molean.isletopia.distribute.parameter.IsletopiaParamter;
import com.molean.isletopia.infrastructure.IsletopiaInfrastructure;
import com.molean.isletopia.infrastructure.individual.MenuCommand;
import com.molean.isletopia.modifier.IsletopiaModifier;
import com.molean.isletopia.protect.IsletopiaProtect;
import com.molean.isletopia.statistics.IsletopiaStatistics;
import com.molean.isletopia.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Objects;

public final class IsletopiaTweakers extends JavaPlugin {

    private static IsletopiaTweakers isletopiaTweakers;
    private static long cur;

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }

    Thread thread = new Thread(() -> {
        try {
            while (true) {
                Thread.sleep(1000 * 10);
                if (System.currentTimeMillis() - cur > 1000 * 60 * 2) {
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
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });


    @Override
    public void onEnable() {
        isletopiaTweakers = this;
        ConfigUtils.setupConfig(this);
        new IsletopiaInfrastructure();
        new IsletopiaModifier();
        new IsletopiaDistributeSystem();
        new IsletopiaParamter();
        new IsletopiaProtect();
        new MenuCommand();
        new IsletopiaBungee();
        new IsletopiaStatistics();
        new IsletopiaAdmin();

//        new IsletopiaStory();
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


        Bukkit.getScheduler().runTaskTimer(this, () -> {
            cur = System.currentTimeMillis();
        }, 0, 60 * 20);

        thread.start();
    }

    @Override
    public void onDisable() {
        thread.interrupt();
    }
}
