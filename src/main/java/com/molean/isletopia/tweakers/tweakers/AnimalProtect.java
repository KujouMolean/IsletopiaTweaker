package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidTriggerEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class AnimalProtect implements Listener {
    private PlotAPI plotAPI;


    public AnimalProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        if (plotAPI == null) {
            plotAPI = new PlotAPI();
        }
        Player player = event.getPlayer();
        PlotPlayer plotPlayer = plotAPI.wrapPlayer(player.getUniqueId());
        Plot currentPlot = plotPlayer.getCurrentPlot();
        if (currentPlot != null) {
            List<UUID> builder = new ArrayList<>();
            UUID owner = currentPlot.getOwner();
            builder.add(owner);
            HashSet<UUID> trusted = currentPlot.getTrusted();
            builder.addAll(trusted);
            if (!builder.contains(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }
}
