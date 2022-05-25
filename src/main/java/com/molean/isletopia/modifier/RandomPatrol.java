package com.molean.isletopia.modifier;

import com.molean.isletopia.shared.annotations.Singleton;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Random;

@Singleton
public class RandomPatrol implements Listener {

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
