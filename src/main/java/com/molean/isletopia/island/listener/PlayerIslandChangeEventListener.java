package com.molean.isletopia.island.listener;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.model.IslandId;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerIslandChangeEventListener implements Listener {

    public PlayerIslandChangeEventListener() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerDataSyncCompleteEvent event) {
        Player player = event.getPlayer();
        LocalIsland toIsland = IslandManager.INSTANCE.getCurrentIsland(event.getPlayer());
        PlayerIslandChangeEvent playerIslandChangeEvent = new PlayerIslandChangeEvent(player, null, toIsland);
        Bukkit.getPluginManager().callEvent(playerIslandChangeEvent);
        if (playerIslandChangeEvent.isCancelled()) {
            MessageUtils.fail(player, "你无法进入该岛屿!");
            player.performCommand("is");
        }

    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LocalIsland fromIsland = IslandManager.INSTANCE.getCurrentIsland(event.getPlayer());
        PlayerIslandChangeEvent playerIslandChangeEvent = new PlayerIslandChangeEvent(player, fromIsland, null);
        Bukkit.getPluginManager().callEvent(playerIslandChangeEvent);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerTeleportEvent event) {
        if(!move(event.getPlayer(), event.getFrom(), event.getTo())){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerMoveEvent event) {
        if(!move(event.getPlayer(), event.getFrom(), event.getTo())){
            event.setCancelled(true);
        }
    }


    // return true if this move should pass
    public boolean move(Player player, Location from, Location to) {
        int fromIslandX = from.getBlockX() >> 9;
        int fromIslandZ = from.getBlockZ() >> 9;

        int toIslandX = to.getBlockX() >> 9;
        int toIslandZ = to.getBlockZ() >> 9;

        if (fromIslandX == toIslandX && fromIslandZ == toIslandZ) {
            return true;
        }
        String serverName = ServerInfoUpdater.getServerName();

        LocalIsland fromIsland = IslandManager.INSTANCE.getLocalIsland(new IslandId(serverName, fromIslandX, fromIslandZ));
        LocalIsland toIsland = IslandManager.INSTANCE.getLocalIsland(new IslandId(serverName, toIslandX, toIslandZ));

        PlayerIslandChangeEvent playerIslandChangeEvent = new PlayerIslandChangeEvent(player, fromIsland, toIsland);
        Bukkit.getPluginManager().callEvent(playerIslandChangeEvent);
        return !playerIslandChangeEvent.isCancelled();
    }


}
