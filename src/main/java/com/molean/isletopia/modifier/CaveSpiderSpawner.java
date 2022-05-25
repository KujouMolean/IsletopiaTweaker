package com.molean.isletopia.modifier;

import com.molean.isletopia.shared.annotations.Singleton;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@Singleton
public class CaveSpiderSpawner implements Listener {
    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!damager.getType().equals(EntityType.LIGHTNING)) {
            return;
        }
        if (!entity.getType().equals(EntityType.SPIDER)) {
            return;
        }
        Location location = entity.getLocation();
        entity.remove();
        location.getWorld().spawnEntity(location, EntityType.CAVE_SPIDER);
    }
}