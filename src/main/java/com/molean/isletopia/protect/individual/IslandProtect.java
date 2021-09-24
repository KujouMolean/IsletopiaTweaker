package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.event.PlayerIslandChangeEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;

public class IslandProtect implements Listener {
    public IslandProtect() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }


    //disable block place
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void on(BlockPlaceEvent event) {
        Location location = event.getBlockPlaced().getLocation();
        Island currentIsland = IslandManager.INSTANCE.getCurrentIsland(location);
        if (currentIsland == null) {
            event.setBuild(false);
        }
        if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), location)) {
            event.setBuild(false);
        }
    }


    //disable interact expect firework
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseInteractedBlock(Event.Result.ALLOW);

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            ItemStack item = event.getItem();
            if (item != null && item.getType().equals(Material.FIREWORK_ROCKET)) {
                return;
            }
        }
        if (event.getClickedBlock() != null) {
            if (!IslandManager.INSTANCE.hasTargetIslandPermission(event.getPlayer(), event.getClickedBlock().getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    //disable collision
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(VehicleEntityCollisionEvent event) {
        if (event.getEntity() instanceof Player player) {
            Location location = event.getEntity().getLocation();
            if (!IslandManager.INSTANCE.hasTargetIslandPermission(player, location)) {
                return;
            }
            event.setCollisionCancelled(true);
        }
    }

    //disable collision
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(PlayerIslandChangeEvent event) {
        Island to = event.getTo();
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
            event.setCancelled(true);
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
}
