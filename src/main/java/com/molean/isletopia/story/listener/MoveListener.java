package com.molean.isletopia.story.listener;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class MoveListener implements Listener {
    private static final Map<String, Boolean> movables = new HashMap<>();

    public static void setMovable(String player, boolean movable) {
        movables.put(player, movable);
    }

    public static void unsetmovable(String player) {
        movables.remove(player);

    }
    public static Boolean isMovable(String player) {
        return movables.get(player);
    }

    public MoveListener() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (movables.containsKey(event.getPlayer().getName()) && !movables.get(event.getPlayer().getName())) {
            Location from = event.getFrom();
            Location to = event.getTo();
            assert to != null;
            event.setTo(new Location(from.getWorld(), from.getX(), from.getBlockY(), from.getBlockZ(), to.getYaw(), to.getPitch()));
        }
    }
}
