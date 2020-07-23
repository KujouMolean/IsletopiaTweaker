package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class PreventCreeperBreak implements Listener {
    public PreventCreeperBreak() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        if (EntityType.CREEPER.equals(event.getEntityType()))
            event.setCancelled(true);
    }
}
