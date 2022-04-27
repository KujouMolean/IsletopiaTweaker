package com.molean.isletopia.island.flag;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Lock implements IslandFlagHandler, Listener {
    public Lock() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler
    public void on(PlayerIslandChangeEvent event) {
        LocalIsland to = event.getTo();
        Player player = event.getPlayer();
        if (to == null || (!to.hasPermission(player) && to.containsFlag("Lock"))) {
            if (event.getPlayer().isOp()) {
                MessageUtils.info(event.getPlayer(), "You are by passing a lock flag!");
                return;
            }
            event.setCancelled(true);
        }
    }

    @Override
    public void onFlagAdd(LocalIsland island, String... data) {
        for (Player player : island.getPlayersInIsland()) {
            if (!island.hasPermission(player)) {
                MessageUtils.strong(player, "island.flag.lock.kick");
                Island playerFirstIsland = IslandManager.INSTANCE.getPlayerFirstIsland(player.getUniqueId());
                if (playerFirstIsland == null) {
                    Tasks.INSTANCE.sync( () -> {
                        player.kick(Component.text("#Severe error, you have no island!"));
                    });
                    return;
                }
                IsletopiaTweakersUtils.universalPlotVisitByMessage(player,playerFirstIsland.getIslandId());
            }
        }

    }
}
