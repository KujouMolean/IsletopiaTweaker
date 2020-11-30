package com.molean.isletopia.statistics.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.VanillaStatisticsDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

public class VanillaStatistic implements Listener {
    public VanillaStatistic() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        VanillaStatisticsDao.checkTable();
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) throws FileNotFoundException {
        String name = event.getPlayer().getName();
        String uuid = event.getPlayer().getUniqueId().toString();
        File worldFolder = event.getPlayer().getWorld().getWorldFolder();
        String serverName = ServerInfoUpdater.getServerName();


        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            try {
                File statsFile = new File(worldFolder + "/stats/" + uuid + ".json");
                FileInputStream inputStream = new FileInputStream(statsFile);
                byte[] bytes = inputStream.readAllBytes();
                String stats = new String(bytes, StandardCharsets.UTF_8);
                VanillaStatisticsDao.setStatistics(serverName, name, stats);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });


    }
}
