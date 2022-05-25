package com.molean.isletopia.infrastructure;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

@Singleton
public class KeepInventory implements Listener{

    private final PlayerPropertyManager playerPropertyManager;
    public KeepInventory(PlayerPropertyManager playerPropertyManager) {
        this.playerPropertyManager = playerPropertyManager;
    }

    @EventHandler

    public void on(PlayerDeathEvent event) {
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getEntity())) {
            event.setKeepInventory(true);
            return;
        }
        if (!playerPropertyManager.getPropertyAsBoolean(event.getEntity(), "DisableKeepInventory")) {

            event.setKeepInventory(true);
            return;
        }
        event.setKeepInventory(false);
    }
}
