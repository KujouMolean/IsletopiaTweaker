package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LastServerUpdater implements Listener {

    public LastServerUpdater() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerDataSyncCompleteEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            UniversalParameter.setParameter(event.getPlayer().getUniqueId(), "lastServer", ServerInfoUpdater.getServerName());
        });
    }
}
