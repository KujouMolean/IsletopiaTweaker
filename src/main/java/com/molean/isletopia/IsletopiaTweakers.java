package com.molean.isletopia;

import com.molean.isletopia.admin.IsletopiaAdmin;
import com.molean.isletopia.bungee.IsletopiaBungee;
import com.molean.isletopia.database.DataSourceUtils;
import com.molean.isletopia.distribute.IsletopiaDistributeSystem;
import com.molean.isletopia.distribute.parameter.IsletopiaParamter;
import com.molean.isletopia.infrastructure.IsletopiaInfrastructure;
import com.molean.isletopia.infrastructure.individual.MenuCommand;
import com.molean.isletopia.message.IsletopiaMessage;
import com.molean.isletopia.modifier.IsletopiaModifier;
import com.molean.isletopia.protect.IsletopiaProtect;
import com.molean.isletopia.shared.BukkitMessageListener;
import com.molean.isletopia.statistics.IsletopiaStatistics;
import com.molean.isletopia.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Objects;

public final class IsletopiaTweakers extends JavaPlugin {

    private static IsletopiaTweakers isletopiaTweakers;

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }

    private static Long l;

    static {
        System.setProperty("druid.mysql.usePingMethod","false");
    }

    @SuppressWarnings("all")
    private static final Thread autoShutDownThread = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
                break;
            }
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
        }

    });


    @Override
    public void onEnable() {
        isletopiaTweakers = this;
        ConfigUtils.setupConfig(this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        DataSourceUtils.checkDatabase();

        new IsletopiaMessage();
        new IsletopiaInfrastructure();
        new IsletopiaModifier();
        new IsletopiaDistributeSystem();
        new IsletopiaParamter();
        new IsletopiaProtect();
        new IsletopiaBungee();
        new IsletopiaStatistics();
        new IsletopiaAdmin();

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            l = System.currentTimeMillis();
        }, 0, 20);

        autoShutDownThread.start();
    }

    @Override
    public void onDisable() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.closeInventory();
        }
        autoShutDownThread.interrupt();
    }
}
