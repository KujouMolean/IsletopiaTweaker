package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RemoveJoinLeftMessage implements Listener {
    public RemoveJoinLeftMessage() {
        PluginUtils.registerEvents(this);
    }
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeft(PlayerQuitEvent event) {
        event.quitMessage(null);
    }
}
