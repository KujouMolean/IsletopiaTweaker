package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnPoint implements Listener {
    public RespawnPoint() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Location location = event.getPlayer().getLocation();
        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);

        if (currentIsland == null) {
            return;
        }

        currentIsland.tp(event.getPlayer());
        event.setRespawnLocation(event.getPlayer().getLocation());
    }
}
