package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
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

import java.util.List;

public class DragonEggLimiter implements Listener {
    private static final List<String> players = List.of("Molean", "__FlandreScarlet", "0731", "cnmrqll", "TSI", "Sirin_");

    public DragonEggLimiter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

    }

    @EventHandler
    public void on(InventoryMoveItemEvent event) {
        if (event.getItem().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getDestination().getLocation();
            Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!players.contains(currentPlot.getOwner())) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getInventory().getLocation();
            Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!players.contains(currentPlot.getOwner())) {
                event.setCancelled(true);
            }
        }


    }

    @EventHandler
    public void on(InventoryDragEvent event) {
        event.getOldCursor();
        if (event.getOldCursor().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getInventory().getLocation();
            Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!players.contains(currentPlot.getOwner())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getInteractionPoint();
            if (location != null) {
                Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
                if (!players.contains(currentPlot.getOwner())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && clickedBlock.getType().equals(Material.DRAGON_EGG)) {
            Location location = clickedBlock.getLocation();
            Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!players.contains(currentPlot.getOwner())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(BlockFromToEvent event) {
        if (event.getBlock().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getToBlock().getLocation();
            Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(location);
            if (!players.contains(currentPlot.getOwner())) {
                event.setCancelled(true);
            }
        }

    }
}
