package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

public class AnimalProtect implements Listener {

    public AnimalProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }


    @EventHandler
    public void on(EntityTargetEvent event) {
        if (event.getTarget() != null && event.getTarget().getType().equals(EntityType.PLAYER)) {
            if (!PlotUtils.hasCurrentPlotPermission((Player) event.getTarget()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRaidTrigger(RaidTriggerEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer()))
            event.setCancelled(true);
        PotionEffect potionEffect = event.getPlayer().getPotionEffect(PotionEffectType.BAD_OMEN);
        event.getPlayer().sendMessage("§c你没有权限触发此岛屿的袭击.");
        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> {
            if (potionEffect == null) {
                return;
            }
            int duration = potionEffect.getDuration() - 100;
            if (duration <= 0) {
                return;
            }
            event.getPlayer().addPotionEffect(potionEffect.withDuration(duration));
        }, 100L);
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

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
