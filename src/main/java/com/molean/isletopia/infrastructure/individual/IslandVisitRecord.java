package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IslandVisitRecord implements Listener {
    public IslandVisitRecord() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler
    public void on(PlayerIslandChangeEvent event) {
        LocalIsland to = event.getTo();
        if (to == null) {
            return;
        }
        Tasks.INSTANCE.async(() -> {
            to.addVisitRecord(event.getPlayer().getName());
        });
    }

}
