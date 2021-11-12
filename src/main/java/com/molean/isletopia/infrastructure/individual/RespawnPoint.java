package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnPoint implements Listener {
    public RespawnPoint() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
    }

    @EventHandler(ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        Location location = event.getPlayer().getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);

        if (currentIsland == null) {
            return;
        }

        currentIsland.tp(event.getPlayer());

        event.setRespawnLocation(event.getPlayer().getLocation());
        event.getPlayer().setBedSpawnLocation(event.getPlayer().getLocation());
    }
}
