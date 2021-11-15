package com.molean.isletopia.island.flag;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SpectatorVisitor implements IslandFlagHandler, Listener {

    public SpectatorVisitor() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @Override
    public void onFlagAdd(LocalIsland island, String... data) {
        for (Player player : island.getPlayersInIsland()) {
            if (!island.hasPermission(player)) {
                if (player.isOp()) {
                    MessageUtils.info(player, "You are by passing a spectator visitor flag!");
                    continue;
                }
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    @Override
    public void onFlagRemove(LocalIsland island, String... data) {
        for (Player player : island.getPlayersInIsland()) {
            if (!island.hasPermission(player) && player.getGameMode().equals(GameMode.SPECTATOR)) {
                if (player.isOp()) {
                    MessageUtils.info(player, "You are by passing a spectator visitor flag!");
                    continue;
                }
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerIslandChangeEvent event) {
        LocalIsland to = event.getTo();
        //island is not null, and island has the flag, and has no permission
        if (to != null && to.containsFlag(getKey()) && !to.hasPermission(event.getPlayer())) {

            if (event.getPlayer().isOp()) {
                MessageUtils.info(event.getPlayer(), "You are by passing a spectator visitor flag!");
                return;
            }
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
        } else {
            if (event.getPlayer().isOp()) {
                return;
            }
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void on(PlayerTeleportEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
            MessageUtils.fail(event.getPlayer(), "你不能使用传送!");
            event.setCancelled(true);
        }
    }
}
