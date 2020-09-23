package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class FertilizeFlower implements Listener {
    public FertilizeFlower() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Block clickedBlock = event.getClickedBlock();
        ItemStack item = event.getItem();
        if (item == null || !item.getType().equals(Material.BONE_MEAL)) {
            return;
        }
        if (clickedBlock == null) {
            return;
        }
        Block relative = clickedBlock.getRelative(BlockFace.UP);
        if (!relative.getType().equals(Material.AIR)) {
            return;
        }
        if (!PlotUtils.hasCurrentPlotPermission(event.getPlayer())) {
            return;
        }
        Random random = new Random();
        if (clickedBlock.getType().equals(Material.POPPY)) {
            Material material;
            if (random.nextBoolean()) {
                if (random.nextBoolean()) {
                    material = Material.ROSE_BUSH;
                } else {
                    material = Material.SUNFLOWER;
                }
            } else {
                if (random.nextBoolean()) {
                    material = Material.LILAC;
                } else {
                    material = Material.PEONY;
                }
            }
            setFlower(clickedBlock, material, Bisected.Half.BOTTOM);
            setFlower(relative, material, Bisected.Half.TOP);
            item.setAmount(item.getAmount() - 1);
            event.setCancelled(true);
        } else if (clickedBlock.getType().equals(Material.WITHER_SKELETON_SKULL)) {
            if (random.nextInt(10) == 0) {
                clickedBlock.setType(Material.WITHER_ROSE);
            } else {
                clickedBlock.setType(Material.POPPY);
            }
            event.setCancelled(true);
        }

    }

    public void setFlower(Block block, Material type, Bisected.Half half) {
        block.setType(type, false);
        Bisected data = (Bisected) block.getBlockData();
        data.setHalf(half);
        block.setBlockData(data);
    }
}
