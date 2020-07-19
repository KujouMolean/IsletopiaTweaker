package com.molean.isletopiatweakers;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Zoglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;

public class RemoveDisgustingMob implements Listener {
    public RemoveDisgustingMob(){
        Bukkit.getPluginManager().registerEvents(this,IsletopiaTweakers.getPlugin());
    }
    @EventHandler
    public void onTransfer(EntityTransformEvent event) {
        if (event.getTransformedEntity() instanceof PigZombie) {
            event.getEntity().remove();
        }
        if (event.getTransformedEntity() instanceof Zoglin) {
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void MobSpawn(EntitySpawnEvent event) {
        if (EntityType.BAT.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
        if (EntityType.ZOMBIFIED_PIGLIN.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
        if (EntityType.ZOGLIN.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
    }
}
