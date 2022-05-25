package com.molean.isletopia.bars;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.DisableTask;
import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.charge.ChargeUtils;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.player.PlayerManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

@Singleton
@CommandAlias("powerbar")
public class ChargeBar extends BaseCommand implements Listener {
    private final HashMap<Player, BossBar> powerBars = new HashMap<>();
    private final PlayerPropertyManager playerPropertyManager;
    private final ChargeCommitter chargeCommitter;

    public ChargeBar(PlayerManager playerManager, PlayerPropertyManager playerPropertyManager, ChargeCommitter chargeCommitter) {
        this.playerPropertyManager = playerPropertyManager;
        this.chargeCommitter = chargeCommitter;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (playerManager.isLogged(onlinePlayer)) {
                checkPlayerBar(onlinePlayer);
            }
        }
    }

    @Interval(value = 20, async = true)
    public void powerBarUpdate() {
        powerBars.forEach((player, bossBar) -> {
            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
            if (currentIsland == null) {
                bossBar.setVisible(false);
                return;
            }
            long totalPower = ChargeUtils.getTotalPower(chargeCommitter.get(currentIsland.getIslandId()));
            long totalPowerUsage = ChargeUtils.getTotalPowerUsage(chargeCommitter.get(currentIsland.getIslandId()));

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
    }

    @DisableTask
    public void disableTask() {
        powerBars.forEach((player, bossBar) -> {
            bossBar.setVisible(false);
            bossBar.removeAll();
        });
    }



    public void checkPlayerBar(Player player) {
        BossBar powerBossBar = powerBars.get(player);
        boolean powerBar = playerPropertyManager.getPropertyAsBoolean(player, "PowerBar");
        if (powerBar) {
            if (powerBossBar == null) {
                powerBars.put(player, Bukkit.createBossBar(null, BarColor.RED, BarStyle.SOLID));
            }
        } else {
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

    @Default
    public void onDefault(Player player) {
        boolean powerBar = playerPropertyManager.getPropertyAsBoolean(player, "PowerBar");
        playerPropertyManager.setPropertyAsync(player, "PowerBar", !powerBar + "", () -> {
            checkPlayerBar(player);
        });
    }

}
