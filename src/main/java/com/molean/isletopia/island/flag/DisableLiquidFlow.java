package com.molean.isletopia.island.flag;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class DisableLiquidFlow implements IslandFlagHandler, Listener {

    public DisableLiquidFlow() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

    }

    @EventHandler
    public void on(BlockFromToEvent event) {
        Location location = event.getBlock().getLocation();
        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            return;
        }
        if (currentIsland.containsFlag(getKey())) {
            event.setCancelled(true);
        }
    }

}
