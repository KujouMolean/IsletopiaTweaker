package com.molean.isletopia.tutor.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class HelpTutor implements Listener {

    private static final Set<Player> PLAYERS = new HashSet<>();
    private static final Map<Player, BossBar> BARS = new HashMap<>();

    public HelpTutor() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        IsletopiaTweakers.addDisableTask("Remove all help tutor bars", () -> {
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
            if (Objects.equals(tutorStatus, "Help") && player.isOnline()) {
                BossBar bossBar = Bukkit.createBossBar(MessageUtils.getMessage(player,"tutor.help.title"), BarColor.GREEN, BarStyle.SEGMENTED_20);
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

    @EventHandler
    public void on(PlayerCommandPreprocessEvent event) {
        if (!PLAYERS.contains(event.getPlayer())) {
            return;
        }

        String message = event.getMessage();

        switch (message.toLowerCase(Locale.ROOT)) {
            case "/is help", "/islet help", "/isletopia help" -> {
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                    MessageUtils.strong(event.getPlayer(), "tutor.help.wiki");
                    MessageUtils.info(event.getPlayer(), "tutor.help.next");
                    PLAYERS.remove(event.getPlayer());
                    BARS.get(event.getPlayer()).removeAll();
                    UniversalParameter.setParameter(event.getPlayer().getUniqueId(), "TutorStatus", "Stone");
                    StoneTutor.onJoin(event.getPlayer());
                });
            }
        }
    }
}
