package com.molean.isletopia.island.flag;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class DisableRedstone implements IslandFlagHandler, Listener {
    public DisableRedstone() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler
    public void on(BlockRedstoneEvent event) {
        Location location = event.getBlock().getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            return;
        }
        if (!currentIsland.containsFlag(getKey())) {
            return;
        }
        event.setNewCurrent(0);
    }
}
