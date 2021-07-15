package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.shared.utils.BukkitBungeeUtils;
import com.molean.isletopia.shared.utils.BukkitPluginUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class PlayerChatTweaker implements Listener {
    public PlayerChatTweaker() {
        Bukkit.getPluginManager().registerEvents(this, BukkitPluginUtils.getPlugin());
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        event.setCancelled(true);
        BukkitBungeeUtils.universalChat(event.getPlayer(), ((TextComponent) event.message()).content());
    }
}
