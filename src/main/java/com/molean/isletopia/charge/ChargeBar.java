package com.molean.isletopia.charge;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class ChargeBar implements CommandExecutor, Listener {
    private static final HashMap<Player, BossBar> powerBars = new HashMap<>();
    private static final HashMap<Player, BossBar> waterBars = new HashMap<>();

    public ChargeBar() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("waterbar")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("powerbar")).setExecutor(this);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            checkPlayerBar(onlinePlayer);
        }
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            powerBars.forEach((player, bossBar) -> {
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    bossBar.setVisible(false);
                    return;
                }
                String owner = currentIsland.getOwner();
                long totalPower = ChargeDetailUtils.getTotalPower(ChargeDetailCommitter.get(owner));
                long totalPowerUsage = ChargeDetailUtils.getTotalPowerUsage(ChargeDetailCommitter.get(owner));
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

            waterBars.forEach((player, bossBar) -> {
                Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    bossBar.setVisible(false);
                    return;
                }
                String owner = currentIsland.getOwner();
                long totalWater = ChargeDetailUtils.getTotalWater(ChargeDetailCommitter.get(owner));
                long totalWaterUsage = ChargeDetailUtils.getTotalWaterUsage(ChargeDetailCommitter.get(owner));
                bossBar.setTitle("水表: " + totalWaterUsage + "/" + totalWater);
                double progress = totalWaterUsage / (double) totalWater;
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

        IsletopiaTweakers.addDisableTask("Stop charge bar, and remove all bars",() -> {
            bukkitTask.cancel();
            powerBars.forEach((player, bossBar) -> {
                bossBar.removeAll();
            });
            waterBars.forEach((player, bossBar) -> {
                bossBar.removeAll();
            });
        });


    }

    public void checkPlayerBar(Player player) {
        String waterBar = UniversalParameter.getParameter(player.getName(), "WaterBar");
        BossBar waterBossBar = waterBars.get(player);
        if (waterBar != null && !waterBar.isEmpty()) {
            if (waterBossBar == null) {
                waterBars.put(player, Bukkit.createBossBar(null, BarColor.BLUE, BarStyle.SOLID));
            }

        } else {
            if (waterBossBar != null) {
                waterBossBar.setVisible(false);
                waterBossBar.removeAll();
                waterBars.remove(player);
            }
        }

        BossBar powerBossBar = powerBars.get(player);
        String powerBar = UniversalParameter.getParameter(player.getName(), "PowerBar");
        if (powerBar != null && !powerBar.isEmpty()) {
            if (powerBossBar == null) {
                powerBars.put(player, Bukkit.createBossBar(null, BarColor.RED, BarStyle.SOLID));
            }
        } else {
            if (powerBossBar != null) {
                powerBossBar.setVisible(false);
                powerBossBar.removeAll();
                powerBars.remove(player);
            }
        }
    }


    @EventHandler
    public void on(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            checkPlayerBar(event.getPlayer());
        });
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        waterBars.remove(event.getPlayer());
        powerBars.remove(event.getPlayer());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;

        if (command.getName().equals("waterbar")) {

            String waterBar = UniversalParameter.getParameter(player.getName(), "WaterBar");
            if (waterBar != null && !waterBar.isEmpty()) {
                UniversalParameter.unsetParameter(player.getName(), "WaterBar");
            } else {
                UniversalParameter.setParameter(player.getName(), "WaterBar", "true");
            }
            checkPlayerBar(player);
        }

        if (command.getName().equals("powerbar")) {
            String powerBar = UniversalParameter.getParameter(player.getName(), "PowerBar");
            if (powerBar != null && !powerBar.isEmpty()) {
                UniversalParameter.unsetParameter(player.getName(), "PowerBar");
            } else {
                UniversalParameter.setParameter(player.getName(), "PowerBar", "true");
            }
            checkPlayerBar(player);
        }


        return true;
    }

}
