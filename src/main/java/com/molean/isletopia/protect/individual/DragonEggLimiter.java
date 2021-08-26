package com.molean.isletopia.protect.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import com.molean.isletopia.utils.UUIDUtils;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.UUID;

public class DragonEggLimiter implements Listener {
    private static final List<String> players = List.of("Molean", "__FlandreScarlet", "0731", "cnmrqll", "TSI", "Sirin_");

    public DragonEggLimiter() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());

    }

    @EventHandler
    public void on(InventoryMoveItemEvent event) {
        if (event.getItem().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getDestination().getLocation();
            Plot currentPlot = PlotUtils.getCurrentPlot(location);
            UUID owner = currentPlot.getOwner();
            String s = UUIDUtils.get(owner);
            if (!players.contains(s)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getInventory().getLocation();
            Plot currentPlot = PlotUtils.getCurrentPlot(location);
            UUID owner = currentPlot.getOwner();
            String s = UUIDUtils.get(owner);
            if (!players.contains(s)) {
                event.setCancelled(true);
            }
        }


    }

    @EventHandler
    public void on(InventoryDragEvent event) {
        event.getOldCursor();
        if (event.getOldCursor().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getInventory().getLocation();
            Plot currentPlot = PlotUtils.getCurrentPlot(location);
            UUID owner = currentPlot.getOwner();
            String s = UUIDUtils.get(owner);
            if (!players.contains(s)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getInteractionPoint();
            if (location != null) {
                Plot currentPlot = PlotUtils.getCurrentPlot(location);
                UUID owner = currentPlot.getOwner();
                String s = UUIDUtils.get(owner);
                if (!players.contains(s)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && clickedBlock.getType().equals(Material.DRAGON_EGG)) {
            Location location = clickedBlock.getLocation();
                Plot currentPlot = PlotUtils.getCurrentPlot(location);
                UUID owner = currentPlot.getOwner();
                String s = UUIDUtils.get(owner);
                if (!players.contains(s)) {
                    event.setCancelled(true);
                }
        }
    }

    @EventHandler
    public void on(BlockFromToEvent event) {
        if (event.getBlock().getType().equals(Material.DRAGON_EGG)) {
            Location location = event.getToBlock().getLocation();
            Plot currentPlot = PlotUtils.getCurrentPlot(location);
            UUID owner = currentPlot.getOwner();
            String s = UUIDUtils.get(owner);
            if (!players.contains(s)) {
                event.setCancelled(true);
            }
        }

    }
}
