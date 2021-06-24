package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;

import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class TestListener implements Listener {
    public TestListener() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }


    @EventHandler
    public void on(PlayerJoinEvent event) {
        System.out.println(event.getPlayer().getUniqueId());
        System.out.println(ServerInfoUpdater.getUUID(event.getPlayer().getName()));
    }
}
