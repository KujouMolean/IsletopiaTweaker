package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class ExplosionProtect implements Listener {
    public ExplosionProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (EntityType.CREEPER.equals(event.getEntityType())) {
            if (!"Creeper".equalsIgnoreCase(event.getEntity().getCustomName())) {
                event.setCancelled(true);
            }
        }
    }
}
