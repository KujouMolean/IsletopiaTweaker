package com.molean.isletopia.statistics.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlayTimeStatisticsDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PlayTime implements Listener {
    private static final Map<String, Long> playerJoinTime = new HashMap<>();

    public PlayTime() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        PlayTimeStatisticsDao.checkTable();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        long timeStamp = Calendar.getInstance().getTimeInMillis();
        playerJoinTime.put(event.getPlayer().getName(), timeStamp);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        String server = ServerInfoUpdater.getServerName();
        long leftTime = Calendar.getInstance().getTimeInMillis();
        Long joinTime = playerJoinTime.get(event.getPlayer().getName());
        if (joinTime == null) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            PlayTimeStatisticsDao.addRecord(name, server, joinTime, leftTime);
        });
    }
}
