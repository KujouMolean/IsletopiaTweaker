package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobRemover implements Listener {
    public MobRemover() {
        PluginUtils.registerEvents(this);
    }

    //remove bat and wither
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(EntitySpawnEvent event) {
        if (EntityType.BAT.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
        if (EntityType.WITHER.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
    }
}
