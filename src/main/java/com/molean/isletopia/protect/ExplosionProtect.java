package com.molean.isletopia.protect;

import com.molean.isletopia.shared.annotations.Singleton;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

@Singleton
public class ExplosionProtect implements Listener {


    //disable creeper explosion cause block break
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (EntityType.CREEPER.equals(event.getEntityType())) {
            if (!"Creeper".equalsIgnoreCase(event.getEntity().getCustomName())) {
                event.setCancelled(true);
            }
        }
    }
}
