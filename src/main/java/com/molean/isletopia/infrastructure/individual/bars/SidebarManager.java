package com.molean.isletopia.infrastructure.individual.bars;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public enum SidebarManager implements Listener {
    INSTANCE;

    SidebarManager() {
        PluginUtils.registerEvents(this);

    }

    private static final Map<UUID, String> map = new HashMap<>();

    public @Nullable String getSidebar(Player player) {
        if (!PlayerPropertyManager.INSTANCE.isLoad(player.getUniqueId())) {
            return null;
        }
        return PlayerPropertyManager.INSTANCE.getProperty(player, "Sidebar");
    }

    public void setSidebar(Player player,String sidebarType) {
        PlayerPropertyManager.INSTANCE.setPropertyAsync(player,"Sidebar" ,sidebarType);
    }

}
