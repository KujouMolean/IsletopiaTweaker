package com.molean.isletopia.island.flag;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class Fly implements IslandFlagHandler, Listener {

    public Fly() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

    }

    @Override
    public void onFlagAdd(Island island, String... data) {
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
    public void onFlagRemove(Island island, String... data) {

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
    public void onIslandChange(PlayerIslandChangeEvent playerIslandChangeEvent) {

        if (playerIslandChangeEvent.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (playerIslandChangeEvent.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            return;
        }

        Island to = playerIslandChangeEvent.getTo();
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
