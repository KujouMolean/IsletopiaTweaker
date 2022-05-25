package com.molean.isletopia.island.flag;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

@Singleton
public class AntiFire implements IslandFlagHandler, Listener {
    @EventHandler
    public void on(BlockBurnEvent event) {
        Block ignitingBlock = event.getIgnitingBlock();
        if (ignitingBlock == null) {
            return;
        }
        Location location = ignitingBlock.getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIslandIfLoaded(location);
        if (currentIsland == null || currentIsland.containsFlag(getKey())) {
            event.setCancelled(true);

        }
    }
}
