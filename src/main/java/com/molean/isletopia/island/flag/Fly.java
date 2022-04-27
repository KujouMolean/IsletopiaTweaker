package com.molean.isletopia.island.flag;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

public class Fly implements IslandFlagHandler, Listener {

    public Fly() {
        PluginUtils.registerEvents(this);
    }

    @Override
    public void onFlagAdd(LocalIsland island, String... data) {
        for (Player player : island.getPlayersInIsland()) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                continue;
            }
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                continue;
            }

            player.setAllowFlight(true);
        }
    }

    @Override
    public void onFlagRemove(LocalIsland island, String... data) {
        for (Player player : island.getPlayersInIsland()) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                continue;
            }
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                continue;
            }
            player.setAllowFlight(false);
        }
    }

    @EventHandler
    public void on(PlayerRespawnEvent event) {
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
            Player player = event.getPlayer();
            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                return;
            }
            if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                return;
            }
            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
            if (currentIsland != null && currentIsland.containsFlag("Fly")) {
                player.setAllowFlight(true);
            } else {
                player.setAllowFlight(false);
            }
        }, 5L);

    }

    @EventHandler
    public void onIslandChange(PlayerIslandChangeEvent playerIslandChangeEvent) {
        if (playerIslandChangeEvent.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (playerIslandChangeEvent.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }
        LocalIsland to = playerIslandChangeEvent.getTo();
        if (to != null) {
            for (String islandFlag : to.getIslandFlags()) {
                String[] split = islandFlag.split("#");
                if (split[0].equals(getKey())) {
                    playerIslandChangeEvent.getPlayer().setAllowFlight(true);
                    return;
                }
            }
        }
        playerIslandChangeEvent.getPlayer().setAllowFlight(false);
    }

    @Override
    public @NotNull String getKey() {
        return "Fly";
    }
}
