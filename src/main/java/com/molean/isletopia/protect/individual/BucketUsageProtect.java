package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public class BucketUsageProtect implements Listener {
    public BucketUsageProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

    }

    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void on(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(player)) {
            event.setCancelled(true);

        }
    }
    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void on(PlayerBucketEntityEvent event) {
        Player player = event.getPlayer();
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(player)) {
            event.setCancelled(true);

        }
    }
    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void on(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(player)) {
            event.setCancelled(true);

        }
    }
}
