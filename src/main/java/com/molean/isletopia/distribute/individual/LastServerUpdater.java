package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerDataSyncCompleteEvent;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LastServerUpdater implements Listener {

    public LastServerUpdater() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerDataSyncCompleteEvent event) {
        Tasks.INSTANCE.async(() -> {
            UniversalParameter.setParameter(event.getPlayer().getUniqueId(), "lastServer", ServerInfoUpdater.getServerName());
        });
    }
}
