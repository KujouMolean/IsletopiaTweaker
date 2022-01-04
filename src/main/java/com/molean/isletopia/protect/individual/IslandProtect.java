package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class IslandProtect implements Listener {
    public IslandProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }


    //disable block place
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(BlockPlaceEvent event) {
        Location location = event.getBlockPlaced().getLocation();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            event.setCancelled(true);
        }
        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), location)) {
            event.setCancelled(true);
        }
    }

    //disable interact expect firework
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getClickedBlock().getLocation())) {
            ItemStack item = event.getItem();
            if (item != null && item.getType().equals(Material.FIREWORK_ROCKET)) {
                event.setUseInteractedBlock(Event.Result.DENY);
                event.setUseItemInHand(Event.Result.ALLOW);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (shooter instanceof Player player) {
            if (!IslandManager.INSTANCE.hasTargetIslandPermission(player, event.getEntity().getLocation())) {
                event.getEntity().remove();
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerInteractEntityEvent event) {

        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(EntityBlockFormEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!IslandManager.INSTANCE.hasTargetIslandPermission(player, event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!IslandManager.INSTANCE.hasTargetIslandPermission(player, event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    //disable vehicle damage
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(VehicleDamageEvent event) {
        if (!(event.getAttacker() instanceof Player player)) {
            return;
        }
        if (!IslandManager.INSTANCE.hasTargetIslandPermission(player, event.getVehicle().getLocation())) {
            event.setCancelled(true);
        }
    }


    //disable armor stand interact
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerArmorStandManipulateEvent event) {
        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    //disable collision
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Player player) {
            Location location = event.getEntity().getLocation();
            if (!IslandManager.INSTANCE.hasTargetIslandPermission(player, location)) {
                event.setCollisionCancelled(true);
            }

        }
    }

    //disable collision
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerIslandChangeEvent event) {
        LocalIsland to = event.getTo();
        event.getPlayer().setCollidable(to != null && to.hasPermission(event.getPlayer()));
    }


    //disable entity move from an island to another island
    @EventHandler
    public void on(EntityMoveEvent event) {
        if (event.getEntityType().equals(EntityType.PLAYER)) {
            return;
        }
        org.bukkit.Location from = event.getFrom();
        org.bukkit.Location to = event.getTo();
        int plotFromX = Math.floorDiv(from.getBlockX(), 512) + 1;
        int plotFromZ = Math.floorDiv(from.getBlockZ(), 512) + 1;
        int plotToX = Math.floorDiv(to.getBlockX(), 512) + 1;
        int plotToZ = Math.floorDiv(to.getBlockZ(), 512) + 1;
        if (plotFromX != plotToX || plotFromZ != plotToZ) {
            event.getEntity().remove();
        }
    }

    //remove vehicle if try to move from an island to another island
    @EventHandler
    public void on(VehicleMoveEvent event) {
        org.bukkit.Location from = event.getFrom();
        org.bukkit.Location to = event.getTo();
        int plotFromX = from.getBlockX() >> 9;
        int plotFromZ = from.getBlockZ() >> 9;
        int plotToX = to.getBlockX() >> 9;
        int plotToZ = to.getBlockZ() >> 9;
        if (plotFromX != plotToX || plotFromZ != plotToZ) {
            event.getVehicle().remove();
        }
    }


    //change to adventure mode
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onChange(PlayerIslandChangeEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        LocalIsland to = event.getTo();
        if (to == null) {
            return;
        }

        if (to.containsFlag("SpectatorVisitor")) {
            return;
        }

        if (to.hasPermission(event.getPlayer())) {
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
        } else {
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
        }


    }
}
