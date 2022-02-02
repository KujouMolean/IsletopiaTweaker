package com.molean.isletopia.island.flag;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

public class AntiFire implements IslandFlagHandler, Listener {
    public AntiFire() {
        PluginUtils.registerEvents(this);
    }
    @EventHandler
    public void on(BlockBurnEvent event) {
        Block ignitingBlock = event.getIgnitingBlock();
        if (ignitingBlock == null) {
            return;
        }
        Location location = ignitingBlock.getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null || currentIsland.containsFlag(getKey())) {
            event.setCancelled(true);

        }
    }
}
