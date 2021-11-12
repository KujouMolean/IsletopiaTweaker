package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.service.UniversalParameter;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.island.IslandManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DragonEggLimiter implements Listener {
    private static final List<UUID> denied = new ArrayList<>();
    private static final List<UUID> allowed = new ArrayList<>();

    public DragonEggLimiter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public boolean allow(UUID uuid) {
        if (denied.contains(uuid)) {
            return false;
        }
        if (allowed.contains(uuid)) {
            return true;
        }

        String dragonEgg = UniversalParameter.getParameter(uuid, "dragonEgg");
        if (!"true".equalsIgnoreCase(dragonEgg)) {
            allowed.add(uuid);
            return true;
        } else {
            denied.add(uuid);
            return false;
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void on(InventoryMoveItemEvent event) {
        if (event.getItem().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getDestination().getLocation();
            LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!allow(currentPlot.getUuid())) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getInventory().getLocation();
            LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!allow(currentPlot.getUuid())) {
                event.setCancelled(true);
            }
        }


    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryDragEvent event) {
        event.getOldCursor();
        if (event.getOldCursor().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getInventory().getLocation();
            LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!allow(currentPlot.getUuid())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getInteractionPoint();
            if (location != null) {
                LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
                if (!allow(currentPlot.getUuid())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && clickedBlock.getType().equals(Material.DRAGON_EGG)) {
            Location location = clickedBlock.getLocation();
            LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!allow(currentPlot.getUuid())) {

                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(BlockFromToEvent event) {
        if (event.getBlock().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getToBlock().getLocation();
            LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!allow(currentPlot.getUuid())) {
                event.setCancelled(true);
            }
        }

    }
}
