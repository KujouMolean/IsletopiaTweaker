package com.molean.isletopia.distribute;

import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.event.PlayerLoggedEvent;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.task.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Singleton
public class LastServerUpdater implements Listener {

    @AutoInject
    private UniversalParameter universalParameter;
    @EventHandler
    public void onPlayerJoin(PlayerLoggedEvent event) {
        Tasks.INSTANCE.async(() -> {
            universalParameter.setParameter(event.getPlayer().getUniqueId(), "lastServer", ServerInfoUpdater.getServerName());
        });
    }
}
