package com.molean.isletopia.tweakers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Visit implements Listener {

    public Visit() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            if (IsletopiaTweakers.getVisits().containsKey(player.getName())) {
                String target = IsletopiaTweakers.getVisits().get(player.getName());
                Bukkit.dispatchCommand(player, "plot visit " + target);
            }
        });
    }

}
