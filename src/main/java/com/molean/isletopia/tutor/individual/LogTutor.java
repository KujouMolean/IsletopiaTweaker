package com.molean.isletopia.tutor.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LogTutor implements Listener {

    private static final Set<Player> PLAYERS = new HashSet<>();
    private static final Map<Player, BossBar> BARS = new HashMap<>();
    private final static int TARGET_LOG = 4;


    public LogTutor() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

        IsletopiaTweakers.addDisableTask("Remove all log tutor bars",() -> {
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
            if (tutorStatus == null || tutorStatus.isEmpty()) {
                UniversalParameter.setParameter(player.getUniqueId(), "TutorStatus", "Log");
                MessageUtils.strong(player, "你已开启新手引导,使用/skiptutor跳过引导.");
                tutorStatus = "Log";
            }

            if (tutorStatus.equals("Log") && player.isOnline()) {
                BossBar bossBar = Bukkit.createBossBar("新手引导: 获取4个木头.", BarColor.GREEN, BarStyle.SEGMENTED_20);
                bossBar.addPlayer(player);
                PLAYERS.add(player);
                BARS.put(player, bossBar);
                updateProgress(player);

            }
        });
    }

    public static void updateProgress(Player player) {
        if (!PLAYERS.contains(player)) {
            return;
        }
        BossBar bossBar = BARS.get(player);
        int pre;
        String parameter = UniversalParameter.getParameter(player.getUniqueId(), "Tutor-Log");
        if (parameter == null || parameter.isEmpty()) {
            pre = 0;
        } else {
            pre = Integer.parseInt(parameter);
        }
        if (pre >= TARGET_LOG) {
            bossBar.removeAll();
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                PLAYERS.remove(player);
                BARS.remove(player);
            });
            UniversalParameter.setParameter(player.getUniqueId(), "TutorStatus", "Help");
            HelpTutor.onJoin(player);
            return;
        }
        double progress = pre / (double) TARGET_LOG;
        if (progress < 0) {
            progress = 0;
        }
        if (progress > 1) {
            progress = 1;
        }
        bossBar.setProgress(progress);
    }

    public static void addProgress(Player player, int amount) {
        int pre;
        String parameter = UniversalParameter.getParameter(player.getUniqueId(), "Tutor-Log");
        if (parameter == null || parameter.isEmpty()) {
            pre = 0;
        } else {
            pre = Integer.parseInt(parameter);
        }
        pre += amount;
        UniversalParameter.setParameter(player.getUniqueId(), "Tutor-Log", pre + "");
        updateProgress(player);
    }



    @EventHandler(ignoreCancelled = true)
    public void on(EntityPickupItemEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.PLAYER)) {
            return;
        }
        if (!event.getItem().getItemStack().getType().name().endsWith("_LOG")) {
            return;
        }
        int amount = event.getItem().getItemStack().getAmount();
        Player player = (Player) entity;
        if (!PLAYERS.contains(player)) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            addProgress(player, amount);
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
