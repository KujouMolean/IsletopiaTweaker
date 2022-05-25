package com.molean.isletopia.island.flag;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

@Singleton
public class DisableBlockBurn implements IslandFlagHandler, Listener {
    @EventHandler
    public void on(BlockBurnEvent event) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIslandIfLoaded(event.getBlock().getLocation());
        if (currentIsland == null) {
            event.setCancelled(true);
            return;
        }
        if (currentIsland.containsFlag(getKey())) {
            event.setCancelled(true);
        }
    }
}
