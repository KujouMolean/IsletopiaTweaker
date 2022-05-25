package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Squid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

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
