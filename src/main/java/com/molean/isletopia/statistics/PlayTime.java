package com.molean.isletopia.statistics.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.database.PlayTimeStatisticsDao;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class PlayTime implements Listener {
    private static final Map<String, Long> playerJoinTime = new HashMap<>();

    public PlayTime() {

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onJoin(onlinePlayer);
        }

        Tasks.INSTANCE.addDisableTask("Commit play time statistics..", () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onQuit(onlinePlayer);
            }
        });

    }

    public void onJoin(Player player) {
        long timeStamp = Calendar.getInstance().getTimeInMillis();
        playerJoinTime.put(player.getName(), timeStamp);
    }

    public void onQuit(Player player) {
        String server = ServerInfoUpdater.getServerName();
        long leftTime = Calendar.getInstance().getTimeInMillis();
        Long joinTime = playerJoinTime.get(player.getName());
        if (joinTime == null) {
            return;
        }
        PlayTimeStatisticsDao.addRecord(player.getUniqueId(), server, joinTime, leftTime);
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoggedEvent event) {
        onJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            onQuit(event.getPlayer());
        });

    }
}
