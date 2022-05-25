package com.molean.isletopia.island.flag;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

@Singleton
public class DisableMobSpawn implements IslandFlagHandler, Listener {
    @EventHandler
    public void on(EntitySpawnEvent event) {
        Location location = event.getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIslandIfLoaded(location);
        if (currentIsland == null) {
            return;
        }
        if (currentIsland.containsFlag(getKey())) {
            event.setCancelled(true);
        }
    }
}
