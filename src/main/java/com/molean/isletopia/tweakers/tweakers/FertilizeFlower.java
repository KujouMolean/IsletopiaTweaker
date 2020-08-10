package com.molean.isletopia.tweakers.tweakers;

import com.molean.isletopia.tweakers.IsletopiaTweakers;
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

public class FertilizeFlower implements Listener {
    public FertilizeFlower() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clickedBlock = event.getClickedBlock();
            ItemStack item = event.getItem();
            if (item != null && item.getType().equals(Material.BONE_MEAL)) {
                if (clickedBlock != null) {
                    Block relative = clickedBlock.getRelative(BlockFace.UP);
                    if (relative.getType().equals(Material.AIR)) {
                        if (clickedBlock.getType().equals(Material.POPPY)) {
                            setFlower(clickedBlock, Material.ROSE_BUSH, Bisected.Half.BOTTOM);
                            setFlower(relative, Material.ROSE_BUSH, Bisected.Half.TOP);
                            item.setAmount(item.getAmount() - 1);
                            event.setCancelled(true);
                        } else if (clickedBlock.getType().equals(Material.DANDELION)) {
                            setFlower(clickedBlock, Material.SUNFLOWER, Bisected.Half.BOTTOM);
                            setFlower(relative, Material.SUNFLOWER, Bisected.Half.TOP);
                            item.setAmount(item.getAmount() - 1);
                            event.setCancelled(true);
                        }
                    }
                }

            }
        }
    }

    public void setFlower(Block block, Material type, Bisected.Half half) {
        block.setType(type, false);
        Bisected data = (Bisected) block.getBlockData();
        data.setHalf(half);
        block.setBlockData(data);
    }
}
