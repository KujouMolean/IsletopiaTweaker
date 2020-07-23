package com.molean.isletopia.prompter.util;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;

import java.util.*;

public class PlotUtils {
    private static PlotAPI plotAPI = new PlotAPI();
    public static Plot getPlot(Player player){
        PlotPlayer wrap = plotAPI.wrapPlayer(player.getName());
        Set<Plot> playerPlots = plotAPI.getPlayerPlots(wrap);
        if(playerPlots.size() < 1)
            return null;
        return playerPlots.iterator().next();
    }

    public static List<String> getTrusted(Plot plot){
        HashSet<UUID> trusted = plot.getTrusted();
        List<String> names = new ArrayList<>();
        for (UUID uuid : trusted) {
            PlotSquared.get().getImpromptuUUIDPipeline().getSingle(uuid, (s, throwable) -> names.add(s));
        }
        return names;
    }
}
