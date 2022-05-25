package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Singleton
public class LastServerUpdater implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerLoggedEvent event) {
        Tasks.INSTANCE.async(() -> {
            UniversalParameter.setParameter(event.getPlayer().getUniqueId(), "lastServer", ServerInfoUpdater.getServerName());
        });
    }
}
