package com.molean.isletopia.prompter.prompter;

import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlotTrustedPrompter extends PlayerPrompter{

    public PlotTrustedPrompter(Player argPlayer, String argTtile) {
        super(argPlayer, argTtile, getPlayerNames(argPlayer));
    }

    private static List<String> getPlayerNames(Player argPlayer) {
        Plot plot = PlotUtils.getPlot(argPlayer);
        if(plot==null){
            return new ArrayList<>();
        }
        return PlotUtils.getTrusted(plot);
    }
}
