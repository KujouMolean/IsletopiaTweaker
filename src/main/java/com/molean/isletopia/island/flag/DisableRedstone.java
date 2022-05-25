package com.molean.isletopia.island.flag;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

@Singleton
public class DisableRedstone implements IslandFlagHandler, Listener {
    @EventHandler
    public void on(BlockRedstoneEvent event) {
        Location location = event.getBlock().getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIslandIfLoaded(location);
        if (currentIsland == null) {
            return;
        }
        if (!currentIsland.containsFlag(getKey())) {
            return;
        }
        event.setNewCurrent(0);
    }
}
