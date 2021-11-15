package com.molean.isletopia.protect.individual;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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


    //disable mob target no permission player
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(EntityTargetEvent event) {
        if (event.getTarget() != null && event.getTarget().getType().equals(EntityType.PLAYER)) {
            if (!IslandManager.INSTANCE.hasTargetIslandPermission((Player) event.getTarget(), event.getEntity().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    //disable player trigger raid
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(RaidTriggerEvent event) {
        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getRaid().getLocation())) {
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
                if (event.getPlayer().isOnline()) {
                    event.getPlayer().addPotionEffect(potionEffect.withDuration(duration));
                }
            }, 100L);
        }
    }


    //disable fire projectile
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player) {
            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(event.getLocation());
            if (!IslandManager.INSTANCE.hasCurrentIslandPermission((Player) shooter)) {
                event.setCancelled(true);
            }
        }
    }

    //disable attack animal
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (!IslandManager.INSTANCE.hasTargetIslandPermission(player, event.getEntity().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    //disable knock back animal
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayer(EntityKnockbackByEntityEvent event) {
        if (event.getHitBy() instanceof Player player) {
            LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
            if (!IslandManager.INSTANCE.hasTargetIslandPermission((Player) event.getHitBy(), event.getEntity().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    //disable firework damage from other player
    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType().equals(EntityType.FIREWORK)) {
            Firework firework = (Firework) event.getDamager();
            ProjectileSource shooter = firework.getShooter();
            if (shooter instanceof Player player) {
                if (!IslandManager.INSTANCE.hasTargetIslandPermission(player, event.getEntity().getLocation())) {
                    event.setCancelled(true);
                }
            }
        }
    }


    //disable player drop item
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerDropItemEvent event) {
        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

}
