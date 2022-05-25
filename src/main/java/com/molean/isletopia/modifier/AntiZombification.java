package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Piglin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

@Singleton
public class AntiZombification implements Listener {

    @EventHandler
    public void MobSpawn(EntitySpawnEvent event) {
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
