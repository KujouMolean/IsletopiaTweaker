package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ClockMenu implements Listener {

    public ClockMenu() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Material material = event.getMaterial();
        Action action = event.getAction();
        if (!material.equals(Material.CLOCK))
            return;
        if (event.getPlayer().isSneaking() && action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            event.getPlayer().performCommand("issue plot visit ${island,%player_name%}");
        }

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            event.getPlayer().performCommand("deluxemenu open main");
        }
        event.setCancelled(true);
    }
}
