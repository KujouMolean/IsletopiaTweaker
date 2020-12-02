package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.generator.ChunkGenerator;

public class MobRemover implements Listener {
    public MobRemover(){
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

    }
}
