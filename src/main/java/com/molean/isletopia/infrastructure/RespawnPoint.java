package com.molean.isletopia.infrastructure;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

@Singleton
public class RespawnPoint implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        Location location = event.getPlayer().getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIslandIfLoaded(location);
        if (currentIsland == null) {
            return;
        }

        currentIsland.tp(event.getPlayer());
        event.setRespawnLocation(event.getPlayer().getLocation());
        event.getPlayer().setBedSpawnLocation(event.getPlayer().getLocation());
    }
}
