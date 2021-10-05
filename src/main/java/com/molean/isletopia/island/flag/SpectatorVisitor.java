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
            if (!island.hasPermission(player)) {
                player.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    @Override
    public void onFlagRemove(Island island, String... data) {
        for (Player player : island.getPlayersInIsland()) {
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerIslandChangeEvent event) {
        Island to = event.getTo();
        if (to != null) {
            for (String islandFlag : to.getIslandFlags()) {
                String[] split = islandFlag.split("#");
                if (split[0].equals(getKey())) {
                    if (!to.hasPermission(event.getPlayer())) {
                        event.getPlayer().setGameMode(GameMode.SPECTATOR);
                        return;
                    }
                }
            }
        }
        if (!event.getPlayer().isOp() || (event.getFrom() != null && event.getFrom().containsFlag(getKey()))) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerTeleportEvent event) {
        if (!event.getPlayer().isOp()) {
            if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE)) {
                MessageUtils.fail(event.getPlayer(), "你不能使用传送!");
                event.setCancelled(true);
            }
        }
    }
}
