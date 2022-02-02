package com.molean.isletopia.infrastructure.individual.bars;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.charge.ChargeDetailCommitter;
import com.molean.isletopia.charge.ChargeDetailUtils;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.service.UniversalParameter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ChargeBar implements CommandExecutor, Listener {
    private static final HashMap<Player, BossBar> powerBars = new HashMap<>();

    public ChargeBar() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("powerbar")).setExecutor(this);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            checkPlayerBar(onlinePlayer);
        }
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            powerBars.forEach((player, bossBar) -> {
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    bossBar.setVisible(false);
                    return;
                }
                long totalPower = ChargeDetailUtils.getTotalPower(ChargeDetailCommitter.get(currentIsland.getIslandId()));
                long totalPowerUsage = ChargeDetailUtils.getTotalPowerUsage(ChargeDetailCommitter.get(currentIsland.getIslandId()));
                bossBar.setTitle("电表: " + totalPowerUsage + "/" + totalPower);
                double progress = totalPowerUsage / (double) totalPower;
                if (progress < 0) {
                    progress = 0;
                }
                if (progress > 1) {
                    progress = 1;
                }
                bossBar.setProgress(progress);
                bossBar.addPlayer(player);
                bossBar.setVisible(true);
            });

        }, 0, 20);

        IsletopiaTweakers.addDisableTask("Stop charge bar, and remove all bars", () -> {
            bukkitTask.cancel();
            powerBars.forEach((player, bossBar) -> {
                bossBar.setVisible(false);
                bossBar.removeAll();
            });
        });


    }

    public void checkPlayerBar(Player player) {
        BossBar powerBossBar = powerBars.get(player);
        String powerBar = UniversalParameter.getParameter(player.getUniqueId(), "PowerBar");
        if (powerBar != null && !powerBar.isEmpty()) {
            if (powerBossBar == null) {
                powerBars.put(player, Bukkit.createBossBar(null, BarColor.RED, BarStyle.SOLID));
            }
        }
    }


    @EventHandler
    public void on(PlayerDataSyncCompleteEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            checkPlayerBar(event.getPlayer());
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        powerBars.remove(event.getPlayer());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        if (command.getName().equals("powerbar")) {
            String powerBar = UniversalParameter.getParameter(player.getUniqueId(), "PowerBar");
            if (powerBar != null && !powerBar.isEmpty()) {
                UniversalParameter.unsetParameter(player.getUniqueId(), "PowerBar");
            } else {
                UniversalParameter.setParameter(player.getUniqueId(), "PowerBar", "true");
            }
            checkPlayerBar(player);
        }


        return true;
    }

}
