package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class IslandEnterMessage implements Listener {

    public IslandEnterMessage() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        int fromPlotX = Math.floorDiv(from.getBlockX(), 512);
        int fromPlotZ = Math.floorDiv(from.getBlockZ(), 512);
        int toPlotX = Math.floorDiv(to.getBlockX(), 512);
        int toPlotZ = Math.floorDiv(to.getBlockZ(), 512);
        if (fromPlotX != toPlotX || fromPlotZ != toPlotZ) {
            Plot plot = PlotSquared.get().getFirstPlotArea().getPlot(new PlotId(toPlotX + 1, toPlotZ + 1));
            if (plot == null) {
                return;
            }
            String title = "§6%1%:%2%,%3%"
                    .replace("%1%", MessageUtils.getLocalServerName())
                    .replace("%2%", toPlotX + 1 + "")
                    .replace("%3%", toPlotZ + 1 + "");
            UUID owner = plot.getOwner();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner);
            String ownerName = offlinePlayer.getName();
            ownerName = ownerName == null ? "未知" : ownerName;
            String subtitle = "§3由 %1% 所有".replace("%1%", ownerName);
            event.getPlayer().sendTitle(title, subtitle, 20, 40, 20);
        }
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Location location = event.getPlayer().getLocation();
        location.add(512, 0, 512);
        PlayerMoveEvent moveEvent = new PlayerMoveEvent(event.getPlayer(), location, event.getPlayer().getLocation());
        on(moveEvent);
    }

}
