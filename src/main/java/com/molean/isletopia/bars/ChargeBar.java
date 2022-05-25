package com.molean.isletopia.infrastructure.individual.bars;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.charge.ChargeDetailCommitter;
import com.molean.isletopia.charge.ChargeDetailUtils;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

@Singleton
public class ChargeBar implements CommandExecutor, Listener {
    private static final HashMap<Player, BossBar> powerBars = new HashMap<>();
    private final PlayerPropertyManager playerPropertyManager;

    public ChargeBar(PlayerManager playerManager, PlayerPropertyManager playerPropertyManager, ChargeDetailCommitter chargeDetailCommitter) {

        this.playerPropertyManager = playerPropertyManager;

        Objects.requireNonNull(Bukkit.getPluginCommand("powerbar")).setExecutor(this);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (playerManager.isLogged(onlinePlayer)) {
                checkPlayerBar(onlinePlayer);
            }
        }
        Tasks.INSTANCE.intervalAsync(20, () -> {
            powerBars.forEach((player, bossBar) -> {
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null) {
                    bossBar.setVisible(false);
                    return;
                }
                long totalPower = ChargeDetailUtils.getTotalPower(chargeDetailCommitter.get(currentIsland.getIslandId()));
                long totalPowerUsage = ChargeDetailUtils.getTotalPowerUsage(chargeDetailCommitter.get(currentIsland.getIslandId()));

                bossBar.setTitle(MessageUtils.getMessage(player, "player.bar.power") + ": " + totalPowerUsage + "/" + totalPower);
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

        });
        Tasks.INSTANCE.addDisableTask("Stop charge bar, and remove all bars", () -> {
            powerBars.forEach((player, bossBar) -> {
                bossBar.setVisible(false);
                bossBar.removeAll();
            });
        });


    }

    public void checkPlayerBar(Player player) {
        BossBar powerBossBar = powerBars.get(player);
        boolean powerBar = playerPropertyManager.getPropertyAsBoolean(player, "PowerBar");
        if (powerBar) {
            if (powerBossBar == null) {
                powerBars.put(player, Bukkit.createBossBar(null, BarColor.RED, BarStyle.SOLID));
            }
        }else{
            if (powerBossBar != null) {
                powerBossBar.removeAll();
                powerBars.remove(player);
            }

        }
    }


    @EventHandler
    public void on(PlayerLoggedEvent event) {
        Tasks.INSTANCE.async(() -> checkPlayerBar(event.getPlayer()));
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        powerBars.remove(event.getPlayer());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        if (command.getName().equals("powerbar")) {
            boolean powerBar = playerPropertyManager.getPropertyAsBoolean(player, "PowerBar");
            playerPropertyManager.setPropertyAsync(player, "PowerBar", !powerBar + "", () -> {
                checkPlayerBar(player);
            });
        }
        return true;
    }

}
