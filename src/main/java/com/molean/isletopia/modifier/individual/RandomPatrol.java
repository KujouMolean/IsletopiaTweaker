package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Random;

public class RandomPatrol implements Listener {
    public RandomPatrol() {
        PluginUtils.registerEvents(this);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntitySpawnEvent event) {
        if (event.getEntity() instanceof WanderingTrader) {
            if (new Random().nextBoolean()) {
                event.setCancelled(true);
                Location location = event.getEntity().getLocation();
                for (int i = 0; i < 4; i++) {
                    Pillager pillager = (Pillager) location.getWorld().spawnEntity(location, EntityType.PILLAGER);
                }
                while (true) {
                    Pillager pillager = (Pillager) location.getWorld().spawnEntity(location, EntityType.PILLAGER);
                    if (!pillager.isPatrolLeader()) {
                        pillager.remove();
                    } else {
                        break;
                    }
                }
            }
        }
    }
}
