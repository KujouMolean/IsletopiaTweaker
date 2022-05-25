package com.molean.isletopia.protect;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

@Singleton
public class BucketUsageProtect implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(player)) {
            event.setCancelled(true);

        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerBucketEntityEvent event) {
        Player player = event.getPlayer();
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(player)) {
            event.setCancelled(true);

        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerBucketFillEvent event) {
        Player player = event.getPlayer();
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(player)) {
            event.setCancelled(true);

        }
    }
}
