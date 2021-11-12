package com.molean.isletopia.tutor.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.infrastructure.individual.bars.ProductionBar;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class MobFarmTutor implements Listener {

    private static final Set<Player> PLAYERS = new HashSet<>();
    private static final Map<Player, BossBar> BARS = new HashMap<>();

    public MobFarmTutor() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            for (Player player : new HashSet<>(PLAYERS)) {
                LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
                if (currentIsland == null || !currentIsland.hasPermission(player)) {
                    continue;
                }
                Map<Material, Integer> materialIntegerMap = ProductionBar.productionPerMin(currentIsland.getIslandId());
                boolean bone = materialIntegerMap.getOrDefault(Material.BONE, 0) > 10;
                boolean rottenFlesh = materialIntegerMap.getOrDefault(Material.ROTTEN_FLESH, 0) > 10;
                boolean gunpowder = materialIntegerMap.getOrDefault(Material.GUNPOWDER, 0) > 10;
                if (bone && rottenFlesh && gunpowder) {
                    BossBar bossBar = BARS.get(player);
                    bossBar.removeAll();
                    PLAYERS.remove(player);
                    BARS.remove(player);

                    MessageUtils.info(player, "看起来你已经建造了一所刷怪塔。");
                    MessageUtils.info(player, "接下来请开洞脑筋，制作一块铁锭。");
                    MessageUtils.info(player, "输入/menu recipe 可查看新合成表，也可用JEI查看。");
                    MessageUtils.info(player, "如果看完新合成表仍然一头雾水，就去梦幻之屿Wiki看看。");

                    UniversalParameter.setParameter(player.getUniqueId(), "TutorStatus", "Iron");
                    IronTutor.onJoin(player);
                }
            }
        }, 30 * 20, 30 * 20);


        IsletopiaTweakers.addDisableTask("Remove all mob farm tutor bars and disable production detect", () -> {
            BARS.forEach((player, bossBar) -> {
                bossBar.removeAll();
            });
            bukkitTask.cancel();
        });
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onJoin(onlinePlayer);
        }

    }

    public static void onJoin(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String tutorStatus = UniversalParameter.getParameter(player.getUniqueId(), "TutorStatus");
            if (Objects.equals(tutorStatus, "MobFarm") && player.isOnline()) {
                BossBar bossBar = Bukkit.createBossBar("新手引导: 建造一个刷怪塔.", BarColor.GREEN, BarStyle.SEGMENTED_20);
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
}
