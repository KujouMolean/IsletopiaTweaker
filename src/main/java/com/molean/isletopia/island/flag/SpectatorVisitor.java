package com.molean.isletopia.island.flag;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SpectatorVisitor implements IslandFlagHandler, Listener {

    public SpectatorVisitor() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @Override
    public void onFlagAdd(Island island, String... data) {
        for (Player player : island.getPlayersInIsland()) {
            if (player.isOp()) {
                continue;
            }

            if (!island.hasPermission(player)) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    @Override
    public void onFlagRemove(Island island, String... data) {
        for (Player player : island.getPlayersInIsland()) {
            if (player.isOp()) {
                return;
            }
            if (!island.hasPermission(player) && player.getGameMode().equals(GameMode.SPECTATOR)) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerIslandChangeEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        Island to = event.getTo();
        if (to != null && to.containsFlag(getKey())) {
            // contains this flag
            if (!to.hasPermission(event.getPlayer())) {
                //and has no permission
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
        } else {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }

    }

    @EventHandler(ignoreCancelled = true)
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
