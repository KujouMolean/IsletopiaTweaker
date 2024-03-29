package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Singleton
public class RemoveJoinLeftMessage implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeft(PlayerQuitEvent event) {
        event.quitMessage(null);
    }
}
