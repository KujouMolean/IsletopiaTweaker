package com.molean.isletopia.bars;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.player.PlayerPropertyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
public class SidebarManager implements Listener {
    private final PlayerPropertyManager playerPropertyManager;
    public SidebarManager(PlayerPropertyManager playerPropertyManager) {
        this.playerPropertyManager = playerPropertyManager;
    }

    public @Nullable String getSidebar(Player player) {
        if (!playerPropertyManager.isLoad(player.getUniqueId())) {
            return null;
        }
        return playerPropertyManager.getProperty(player, "Sidebar");
    }

    public void setSidebar(Player player,String sidebarType) {
        playerPropertyManager.setPropertyAsync(player,"Sidebar" ,sidebarType);
    }

}
