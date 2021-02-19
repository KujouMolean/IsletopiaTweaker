package com.molean.isletopia.protect.individual;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

public class OtherProtect implements Listener {
    public OtherProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerTakeLecternBookEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(BeaconEffectEvent event) {
        Location location = event.getBlock().getLocation();
        Plot currentPlot = PlotUtils.getCurrentPlot(location);
        if (!PlotUtils.hasPlotPermission(currentPlot, event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
