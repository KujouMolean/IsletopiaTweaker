package com.molean.isletopia.island.flag;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Lock implements IslandFlagHandler, Listener {
    public Lock() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerIslandChangeEvent event) {
        Island to = event.getTo();
        Player player = event.getPlayer();
        if (event.getPlayer().isOp()) {
            return;
        }
        if (to == null || (!to.hasPermission(player) && to.containsFlag("Lock"))) {
            event.setCancelled(true);
        }

    }

    @Override
    public void onFlagAdd(Island island, String... data) {
        for (Player player : island.getPlayersInIsland()) {
            if (!island.hasPermission(player)) {
                IsletopiaTweakersUtils.universalPlotVisitByMessage(player, player.getName(), 0);
            }
        }

    }
}
