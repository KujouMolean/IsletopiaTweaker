package com.molean.isletopia.island.flag;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

public class DisableBlockBurn implements IslandFlagHandler, Listener {

    public DisableBlockBurn() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler
    public void on(BlockBurnEvent event) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(event.getBlock().getLocation());
        if (currentIsland == null) {
            return;
        }
        if (currentIsland.containsFlag(getKey())) {
            event.setCancelled(true);
        }
    }
}
