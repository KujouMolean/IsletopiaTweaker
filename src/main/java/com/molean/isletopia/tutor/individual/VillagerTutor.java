package com.molean.isletopia.tutor.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class VillagerTutor implements Listener {
    private static final Set<Player> PLAYERS = new HashSet<>();
    private static final Map<Player, BossBar> BARS = new HashMap<>();

    public VillagerTutor() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        IsletopiaTweakers.addDisableTask("Remove all villager tutor bars", () -> {
            BARS.forEach((player, bossBar) -> {
                bossBar.removeAll();
            });
        });
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onJoin(onlinePlayer);
        }
    }

    public static void onJoin(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String tutorStatus = UniversalParameter.getParameter(player.getUniqueId(), "TutorStatus");
            if (Objects.equals(tutorStatus, "Villager") && player.isOnline()) {
                BossBar bossBar = Bukkit.createBossBar("新手引导: 拯救一只村民.", BarColor.GREEN, BarStyle.SEGMENTED_20);
                bossBar.addPlayer(player);
                PLAYERS.add(player);
                BARS.put(player, bossBar);
            }
        });
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        onJoin(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        onQuit(event.getPlayer());
    }

    public static void onQuit(Player player) {
        PLAYERS.remove(player);
        BossBar bossBar = BARS.get(player);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(CreatureSpawnEvent event) {
        if (!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CURED)) {
            return;
        }
        LocalIsland island = IslandManager.INSTANCE.getCurrentIsland(event.getLocation());
        if (island == null) {
            return;
        }
        for (Player player : island.getPlayersInIsland()) {
            if (island.hasPermission(player) && PLAYERS.contains(player)) {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    MessageUtils.info(player, "恭喜你完成了新手引导。");
                    MessageUtils.info(player, "接下来时光请自行探索。");
                    PLAYERS.remove(player);
                    BARS.get(player).removeAll();
                    UniversalParameter.setParameter(player.getUniqueId(), "TutorStatus", "Done");
                });
            }
        }
    }
}
