package com.molean.isletopia.tweakers;

import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.location.BlockLoc;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class PlotUtils {
    private static PlotAPI plotAPI;

    public static Plot getCurrentPlot(Player player) {
        if (plotAPI == null) {
            plotAPI = new PlotAPI();
        }
        PlotPlayer plotPlayer = plotAPI.wrapPlayer(player.getUniqueId());
        return plotPlayer.getCurrentPlot();
    }

    public static boolean hasCurrentPlotPermission(Player player) {
        Plot currentPlot = getCurrentPlot(player);
        if (currentPlot == null)
            return false;
        List<UUID> builder = new ArrayList<>();
        UUID owner = currentPlot.getOwner();
        builder.add(owner);
        HashSet<UUID> trusted = currentPlot.getTrusted();
        builder.addAll(trusted);
        return builder.contains(player.getUniqueId());
    }

    public static boolean isCurrentPlotOwner(Player player) {
        Plot currentPlot = getCurrentPlot(player);
        if (currentPlot == null)
            return false;
        UUID owner = currentPlot.getOwner();
        return player.getUniqueId().equals(owner);
    }
}
