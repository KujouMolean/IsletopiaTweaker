package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KeepInventory implements Listener{
    public KeepInventory() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getEntity())) {
            event.setKeepInventory(true);
            return;
        }
        if (!PlayerPropertyManager.INSTANCE.getPropertyAsBoolean(event.getEntity(), "DisableKeepInventory")) {
            event.setKeepInventory(true);
            return;
        }
        event.setKeepInventory(false);
    }
}
