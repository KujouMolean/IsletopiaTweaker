package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.task.Tasks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Singleton
public class IslandVisitRecord implements Listener {

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
