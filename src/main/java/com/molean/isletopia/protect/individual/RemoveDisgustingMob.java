package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.generator.ChunkGenerator;

public class RemoveDisgustingMob implements Listener {
    public RemoveDisgustingMob(){
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void MobSpawn(EntitySpawnEvent event) {
        if (EntityType.BAT.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
        if (EntityType.WITHER.equals(event.getEntity().getType())) {
            event.setCancelled(true);
        }
        if (EntityType.HOGLIN.equals(event.getEntity().getType())) {
            Hoglin hoglin = (Hoglin) event.getEntity();
            hoglin.setImmuneToZombification(true);
        }
        if (EntityType.PIGLIN.equals(event.getEntity().getType())) {
            Piglin piglin = (Piglin) event.getEntity();
            piglin.setImmuneToZombification(true);
        }
    }
}
