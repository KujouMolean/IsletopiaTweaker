package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Squid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class DeepOceanGuardian implements Listener {
    public DeepOceanGuardian() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Squid))
            return;
        Location location = event.getLocation();
        if (!location.getBlock().getBiome().getKey().getKey().toLowerCase().contains("deep"))
            return;
        location.getWorld().spawnEntity(location, EntityType.GUARDIAN);
        event.getEntity().remove();
    }
}
