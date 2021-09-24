package com.molean.isletopia.protect.individual;

import com.destroystokyo.paper.event.block.BeaconEffectEvent;
import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.utils.PlotUtils;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import io.papermc.paper.event.player.PlayerChangeBeaconEffectEvent;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import io.papermc.paper.event.player.PlayerNameEntityEvent;
import io.papermc.paper.event.player.PlayerPurchaseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class OtherProtect implements Listener {
    public OtherProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerTakeLecternBookEvent event) {
//        if (event.getPlayer().isOp()) {
//            return;
//        }
//        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getPlayer().getBedLocation())) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(BeaconEffectEvent event) {
//        if (event.getPlayer().isOp()) {
//            return;
//        }
//        Location location = event.getBlock().getLocation();
//        Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
//        if (currentPlot == null || !currentPlot.hasPermission(event.getPlayer())) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerFishEvent event) {
//        if (BeaconIslandOption.isEnablePvP(IslandManager.INSTANCE.getCurrentIsland(event.getPlayer()))) {
//            return;
//        }
//        if (event.getPlayer().isOp()) {
//            return;
//        }
//        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getHook().getLocation())) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerShearBlockEvent event) {
//        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getBlock().getLocation())) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerShearEntityEvent event) {
//        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getEntity().getLocation())) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerNameEntityEvent event) {
//        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getEntity().getLocation())) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerHarvestBlockEvent event) {
//        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getHarvestedBlock().getLocation())) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerFlowerPotManipulateEvent event) {
//        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getFlowerpot().getLocation())) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerPurchaseEvent event) {
//        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getPlayer())) {
//            event.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerChangeBeaconEffectEvent event) {
//        if (event.getBeacon() != null) {
//            if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getBeacon().getLocation())) {
//                event.setCancelled(true);
//            }
//        }
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    public void on(PlayerLaunchProjectileEvent event) {
//        boolean fireball = event.getProjectile().getType().equals(EntityType.FIREWORK);
//        if (fireball) {
//            return;
//        }
//
//        if (!IslandManager.INSTANCE.hasCurrentIslandPermission(event.getPlayer())) {
//            event.setCancelled(true);
//        }
//    }



}
