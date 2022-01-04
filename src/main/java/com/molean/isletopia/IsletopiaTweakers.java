package com.molean.isletopia;

import com.molean.isletopia.admin.IsletopiaAdmin;
import com.molean.isletopia.charge.IsletopiaChargeSystem;
import com.molean.isletopia.distribute.IsletopiaDistributeSystem;
import com.molean.isletopia.distribute.parameter.IsletopiaParameter;
import com.molean.isletopia.infrastructure.IsletopiaInfrastructure;
import com.molean.isletopia.island.IsletopiaIslandSystem;
import com.molean.isletopia.mail.MailCommand;
import com.molean.isletopia.message.IsletopiaMessage;
import com.molean.isletopia.modifier.IsletopiaModifier;
import com.molean.isletopia.other.CommandListener;
import com.molean.isletopia.protect.IsletopiaProtect;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.statistics.IsletopiaStatistics;
import com.molean.isletopia.tutor.IsletopiaTutorialSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public final class IsletopiaTweakers extends JavaPlugin {

    private static IsletopiaTweakers isletopiaTweakers;

    public static IsletopiaTweakers getPlugin() {
        return isletopiaTweakers;
    }

    private static World world;

    public static World getWorld() {
        return world;
    }

    private static final Map<String, Runnable> SHUTDOWN_MAP = new HashMap<>();

    public static void addDisableTask(String key, Runnable runnable) {
        SHUTDOWN_MAP.put(key, runnable);
    }

    @Override
    public void onEnable() {
        isletopiaTweakers = this;
        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            world = Bukkit.getWorld("SkyWorld");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            new IsletopiaMessage();
            new IsletopiaInfrastructure();
            new IsletopiaModifier();
            new IsletopiaDistributeSystem();
            new IsletopiaParameter();
            new IsletopiaProtect();
            new IsletopiaStatistics();
            new IsletopiaAdmin();
            new IsletopiaChargeSystem();
            new IsletopiaIslandSystem();
            RedisMessageListener.init();
            new CommandListener();
            new MailCommand();
            new IsletopiaTutorialSystem();
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
        SHUTDOWN_MAP.forEach((s, runnable) -> {
            getLogger().info("Running shutdown task: " + s);
            long l = System.currentTimeMillis();
            runnable.run();
            getLogger().info(s + " complete in " + (System.currentTimeMillis() - l) + "ms");
        });
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.setGameMode(GameMode.SPECTATOR);
        }
    }
}
