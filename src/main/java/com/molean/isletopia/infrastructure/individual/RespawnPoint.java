package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnPoint implements Listener {
    public RespawnPoint() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Plot currentPlot = PlotUtils.getCurrentPlot(event.getPlayer());
        com.plotsquared.core.location.Location home = currentPlot.getHomeSynchronous();
        event.setRespawnLocation(new Location(
                event.getPlayer().getWorld(),
                home.getX() + 0.5,
                home.getY(),
                home.getZ() + 0.5,
                home.getYaw(),
                home.getPitch()));
    }
}
