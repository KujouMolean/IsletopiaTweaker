package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.infrastructure.individual.MessageUtils;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class PlotUtils {
    private static final PlotAPI plotAPI = new PlotAPI();

    public static Plot getCurrentPlot(Player player) {
        @SuppressWarnings("all") PlotPlayer plotPlayer = plotAPI.wrapPlayer(player.getUniqueId());
        if (plotPlayer == null) return null;
        return plotPlayer.getCurrentPlot();
    }


    public static Plot getCurrentPlot(Location location) {
        String world = location.getWorld().getName();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        PlotArea plotArea = plotAPI.getPlotSquared().getFirstPlotArea();
        return plotArea.getPlot(new com.plotsquared.core.location.Location(world, x, y, z));
    }

    public static boolean hasPlotPermission(Plot plot, Player player) {
        if (plot == null) {
            return false;
        }
        List<UUID> builder = new ArrayList<>();
        UUID owner = plot.getOwner();
        builder.add(owner);
        HashSet<UUID> trusted = plot.getTrusted();
        builder.addAll(trusted);
        return builder.contains(player.getUniqueId());
    }


    @SuppressWarnings("all")
    public static boolean hasCurrentPlotPermission(Player player) {
        Plot currentPlot = getCurrentPlot(player);
        return hasPlotPermission(currentPlot,player);

    }

    @SuppressWarnings("all")
    public static boolean isCurrentPlotOwner(Player player) {
        Plot currentPlot = getCurrentPlot(player);
        if (currentPlot == null)
            return false;
        UUID owner = currentPlot.getOwner();
        return player.getUniqueId().equals(owner);
    }

    public static Plot getPlot(Player player) {
        return getPlot(player.getName());
    }

    public static Plot getPlot(String player) {
        String server = ServerInfoUpdater.getServerName();
        PlotId plotId = PlotDao.getPlotPosition(server, player);
        return PlotSquared.get().getFirstPlotArea().getPlot(plotId);
    }

    public static List<String> getTrusted(Plot plot) {
        HashSet<UUID> trusted = plot.getTrusted();
        List<String> names = new ArrayList<>();
        for (UUID uuid : trusted) {
            PlotSquared.get().getImpromptuUUIDPipeline().getSingle(uuid, (s, throwable) -> names.add(s));
        }
        return names;
    }

    public static void localServerTeleport(Player source, String target) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            @SuppressWarnings("all") PlotPlayer sourcePlayer = plotAPI.wrapPlayer(source.getUniqueId());
            Plot plot = getPlot(target);
            plot.teleportPlayer(sourcePlayer, TeleportCause.PLUGIN, aBoolean -> {
                String localServerName = MessageUtils.getLocalServerName();
                PlotId id = plot.getId();
                String title = "§6%1%:%2%,%3%"
                        .replace("%1%", localServerName)
                        .replace("%2%", id.getX() + "")
                        .replace("%3%", id.getY() + "");
                String subtitle = MessageUtils.getMessage("island.subtitle").replace("%1%", target);
                source.sendTitle(title, subtitle, 20, 40, 20);
            });
        });
    }

    public static boolean hasPlot(String player) {
        List<String> servers = new ArrayList<>(ServerInfoUpdater.getServers());
        for (String server : servers) {
            if (!server.startsWith("server"))
                continue;
            Integer plotID = PlotDao.getPlotID(server, player);
            if (plotID != null) {
                return true;
            }
        }
        return false;
    }


}
