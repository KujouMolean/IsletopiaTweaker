package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.projectiles.ProjectileSource;

public class AnimalProtect implements Listener {

    public AnimalProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player) {
            if (!PlotUtils.hasCurrentPlotPermission((Player) shooter)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            if (!PlotUtils.hasCurrentPlotPermission((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }
}
