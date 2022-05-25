package com.molean.isletopia.modifier;

import com.molean.isletopia.shared.annotations.Singleton;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

@Singleton
public class DeepOceanGuardian implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void on(CreatureSpawnEvent event) {
        if (!(event.getEntityType().equals(EntityType.SQUID))) {
            return;
        }
        Location location = event.getLocation();
        if (!location.getBlock().getBiome().getKey().getKey().toLowerCase().contains("deep"))
            return;
        location.getWorld().spawnEntity(location, EntityType.GUARDIAN, CreatureSpawnEvent.SpawnReason.CUSTOM);

        event.getEntity().remove();
    }
}
