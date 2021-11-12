package com.molean.isletopia.infrastructure.individual.bars;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import org.bukkit.Bukkit;
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
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

    }

    private static final Map<UUID, String> map = new HashMap<>();

    public @Nullable String getSidebar(UUID uuid) {
        String s = map.get(uuid);
        if (s == null) {
            s = UniversalParameter.getParameter(uuid, "Sidebar");
            map.put(uuid, s);
        }
        return s;
    }

    public void setSidebar(UUID uuid,String sidebarType) {
        UniversalParameter.setParameter(uuid, "Sidebar", sidebarType);
        map.remove(uuid);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        map.remove(event.getPlayer().getUniqueId());
    }
}
