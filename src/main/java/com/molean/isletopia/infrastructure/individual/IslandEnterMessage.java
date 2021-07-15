package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class IslandEnterMessage implements Listener {

    public IslandEnterMessage() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void sendEnterMessage(Player player, int toPlotX, int toPlotZ) {
        Plot plot = PlotUtils.getFirstPlotArea().getPlot(PlotId.of(toPlotX + 1, toPlotZ + 1));
        if (plot == null) {
            return;
        }
        String alias = plot.getAlias();
        String title;
        if (alias.isEmpty()) {
            title = "§6%1%:%2%,%3%"
                    .replace("%1%", MessageUtils.getLocalServerName())
                    .replace("%2%", toPlotX + 1 + "")
                    .replace("%3%", toPlotZ + 1 + "");
        } else {
            title = "§6%1%:%2%"
                    .replace("%1%", MessageUtils.getLocalServerName())
                    .replace("%2%", alias);
        }

        UUID owner = plot.getOwner();
        if (owner == null) {
            return;
        }
        PlotSquared.get().getImpromptuUUIDPipeline().getSingle(owner, (ownerName, throwable) -> {
            ownerName = ownerName == null ? "未知" : ownerName;
            String subtitle = "§3由 %1% 所有".replace("%1%", ownerName);
            player.sendTitle(title, subtitle, 20, 40, 20);
        });
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            int fromPlotX = Math.floorDiv(from.getBlockX(), 512);
            int fromPlotZ = Math.floorDiv(from.getBlockZ(), 512);
            int toPlotX = Math.floorDiv(to.getBlockX(), 512);
            int toPlotZ = Math.floorDiv(to.getBlockZ(), 512);
            if (fromPlotX != toPlotX || fromPlotZ != toPlotZ) {
                sendEnterMessage(event.getPlayer(), toPlotX, toPlotZ);
            }
        });

    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Location to = event.getPlayer().getLocation();
        int toPlotX = Math.floorDiv(to.getBlockX(), 512);
        int toPlotZ = Math.floorDiv(to.getBlockZ(), 512);
        sendEnterMessage(event.getPlayer(), toPlotX, toPlotZ);
    }

}
