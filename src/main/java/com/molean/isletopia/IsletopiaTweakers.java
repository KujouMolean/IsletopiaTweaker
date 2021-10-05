package com.molean.isletopia;

import com.molean.isletopia.admin.IsletopiaAdmin;
import com.molean.isletopia.charge.IsletopiaChargeSystem;
import com.molean.isletopia.database.DataSourceUtils;
import com.molean.isletopia.distribute.IsletopiaDistributeSystem;
import com.molean.isletopia.distribute.parameter.IsletopiaParamter;
import com.molean.isletopia.infrastructure.IsletopiaInfrastructure;
import com.molean.isletopia.island.IsletopiaIslandSystem;
import com.molean.isletopia.mail.MailCommand;
import com.molean.isletopia.message.IsletopiaMessage;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.modifier.IsletopiaModifier;
import com.molean.isletopia.other.CommandListener;
import com.molean.isletopia.protect.IsletopiaProtect;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.utils.RedisUtils;
import com.molean.isletopia.statistics.IsletopiaStatistics;
import com.molean.isletopia.tutor.IsletopiaTutorialSystem;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    private static final Map<String, Runnable> SHUTDOWN_MAP = new HashMap<>();


    public static void addDisableTask(String key, Runnable runnable) {
        SHUTDOWN_MAP.put(key, runnable);
    }

    public static void removeDisableTask(String key) {
        SHUTDOWN_MAP.remove(key);
    }

    @Override
    public void onEnable() {
        isletopiaTweakers = this;
        Bukkit.getScheduler().runTask(getPlugin(), () -> {
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
            new IsletopiaIslandSystem();
            RedisMessageListener.init();
            new CommandListener();
            new MailCommand();
            new IsletopiaTutorialSystem();

//            auto shutdown
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                l = System.currentTimeMillis();
            }, 0, 20);
            autoShutDownThread.start();
        });
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new EmptyChunkGenerator();
    }

    @Override
    public void onDisable() {
        getLogger().info("Destroy redis listener..");
        RedisMessageListener.destroy();
        getLogger().info("Close online player inventory..");
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.closeInventory();
        }
        getLogger().info("Add restart flag to redis..");
        RedisUtils.getCommand().setex("Restarting-" + ServerInfoUpdater.getServerName(), 15L, "true");
        SHUTDOWN_MAP.forEach((s, runnable) -> {
            getLogger().info("Running shutdown task: " + s);
            runnable.run();
        });
        getLogger().info("Disable auto shutdown thread..");
        autoShutDownThread.interrupt();
        RedisUtils.destroy();

    }
}
