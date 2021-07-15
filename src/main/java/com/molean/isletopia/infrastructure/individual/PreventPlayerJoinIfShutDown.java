package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import net.craftersland.data.bridge.PD;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PreventPlayerJoinIfShutDown implements Listener {

    public PreventPlayerJoinIfShutDown() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (PD.isDisabling) {
            event.getPlayer().kick(Component.text("服务器正在重启中.."));
        }
    }
}
