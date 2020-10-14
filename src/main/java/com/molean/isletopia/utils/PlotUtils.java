package com.molean.isletopia.utils;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.ParameterDao;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class PlotUtils {
    private static final PlotAPI plotAPI = new PlotAPI();

    public static Plot getCurrentPlot(Player player) {
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

    public static Plot getPlot(Player player) {
        PlotPlayer wrap = plotAPI.wrapPlayer(player.getName());
        Set<Plot> playerPlots = plotAPI.getPlayerPlots(wrap);
        if (playerPlots.size() < 1)
            return null;
        return playerPlots.iterator().next();
    }

    public static List<String> getTrusted(Plot plot) {
        HashSet<UUID> trusted = plot.getTrusted();
        List<String> names = new ArrayList<>();
        for (UUID uuid : trusted) {
            PlotSquared.get().getImpromptuUUIDPipeline().getSingle(uuid, (s, throwable) -> names.add(s));
        }
        return names;
    }

    public static void localServerTeleport(Player player, String target) {
        Set<Plot> playerPlots = plotAPI.getPlayerPlots(plotAPI.wrapPlayer(ServerInfoUpdater.getUUID(target)));
        Iterator<Plot> iterator = playerPlots.iterator();
        if (iterator.hasNext()) {
            Plot first = iterator.next();
            first.teleportPlayer(plotAPI.wrapPlayer(player.getUniqueId()), TeleportCause.PLUGIN, (b) -> {
            });
        }
    }
}
