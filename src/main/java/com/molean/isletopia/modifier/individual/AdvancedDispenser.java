package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;

public class AdvancedDispenser implements Listener {
    public AdvancedDispenser() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onDispenser(BlockDispenseEvent event) {
        Directional directional = (Directional) event.getBlock().getBlockData();
        ItemStack item = event.getItem();
        Block relative = event.getBlock().getRelative(directional.getFacing());
        if (item.getType().isBlock()) {
            if (relative.getType().isAir()) {
                relative.setType(item.getType());
                Dispenser dispenser = (Dispenser) event.getBlock().getState();
                Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                    for (ItemStack stack : dispenser.getInventory().getContents()) {
                        if (stack.isSimilar(item)) {
                            stack.setAmount(stack.getAmount() - 1);
                            break;
                        }
                    }
                });
            }
            event.setCancelled(true);
        }
    }
}
