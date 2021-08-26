package com.molean.isletopia;

import com.molean.isletopia.admin.IsletopiaAdmin;
import com.molean.isletopia.charge.IsletopiaChargeSystem;
import com.molean.isletopia.database.DataSourceUtils;
import com.molean.isletopia.distribute.IsletopiaDistributeSystem;
import com.molean.isletopia.distribute.parameter.IsletopiaParamter;
import com.molean.isletopia.infrastructure.IsletopiaInfrastructure;
import com.molean.isletopia.mail.MailCommand;
import com.molean.isletopia.message.IsletopiaMessage;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.modifier.IsletopiaModifier;
import com.molean.isletopia.other.CommandListener;
import com.molean.isletopia.protect.IsletopiaProtect;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.statistics.IsletopiaStatistics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.logging.Logger;

public final class IsletopiaTweakers extends JavaPlugin {

    private static IsletopiaTweakers isletopiaTweakers;

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }

    private static World world;

    public static World getWorld() {
        return world;
    }

    private static Long l;

    static {
        System.setProperty("druid.mysql.usePingMethod", "false");
    }

    @SuppressWarnings("all")
    private static final Thread autoShutDownThread = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(60 * 1000);
            } catch (InterruptedException e) {
                return;
            }
            if (System.currentTimeMillis() - l > 1000 * 60) {
                new Thread(() -> {
                    try {
                        Thread.sleep(60 * 1000);
                        Class<?> aClass = Class.forName("java.lang.Shutdown");
                        Method halt = aClass.getDeclaredMethod("halt", int.class);
                        halt.setAccessible(true);
                        halt.invoke(null, -1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
                Bukkit.getScheduler().runTask(getPlugin(), () -> {
                    Objects.requireNonNull(Bukkit.getWorld("SkyWorld")).save();
                    Bukkit.shutdown();
                });
            }
        }

    });


    @Override
    public void onEnable() {
        isletopiaTweakers = this;
        world = Bukkit.getWorld("SkyWorld");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        DataSourceUtils.checkDatabase();

        new IsletopiaMessage();
        new IsletopiaInfrastructure();
        new IsletopiaModifier();
        new IsletopiaDistributeSystem();
        new IsletopiaParamter();
        new IsletopiaProtect();
        new IsletopiaStatistics();
        new IsletopiaAdmin();
        new IsletopiaChargeSystem();


        RedisMessageListener.init();

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            l = System.currentTimeMillis();
        }, 0, 20);

        autoShutDownThread.start();


        //test
        new CommandListener();
        new MailCommand();


    }

    @Override
    public void onDisable() {
        RedisMessageListener.destroy();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.closeInventory();
        }
        try (Jedis jedis = RedisUtils.getJedis()) {
            jedis.setex("Restarting-" + ServerInfoUpdater.getServerName(), 15L, "true");
        }
        autoShutDownThread.interrupt();
    }
}
