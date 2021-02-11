package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class AdvancedDispenser implements Listener {
    public AdvancedDispenser() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }


    @EventHandler(ignoreCancelled = true)
    public void onDispenser(BlockDispenseEvent event) {
        System.out.println(System.currentTimeMillis());
        Directional directional = (Directional) event.getBlock().getBlockData();
        ItemStack item = event.getItem();
        Block relative = event.getBlock().getRelative(directional.getFacing());
        List<MetadataValue> metadata = event.getBlock().getMetadata("advanced-dispenser-cooldown");
        if (metadata.size() > 0) {
            if (System.currentTimeMillis() - metadata.get(0).asInt() > 50) {
                event.getBlock().removeMetadata("advanced-dispenser-cooldown", IsletopiaTweakers.getPlugin());
                System.out.println("remove cooldown");
            }
            event.setCancelled(true);
            System.out.println("cancel event");
            return;
        }
        if (!item.getType().isBlock()) {
            return;
        }
        if (!relative.getType().isAir()) {
            return;
        }
        Dispenser dispenser = (Dispenser) event.getBlock().getState();
        for (ItemStack stack : dispenser.getInventory().getContents()) {
            if (stack == null || !stack.isSimilar(item)) {
                continue;
            }
            relative.setType(item.getType(), true);
            stack.setAmount(stack.getAmount() - 1);
            BlockData cloneData = dispenser.getBlockData().clone();
            event.getBlock().setType(Material.DISPENSER);
            event.getBlock().setBlockData(cloneData);
            FixedMetadataValue data = new FixedMetadataValue(IsletopiaTweakers.getPlugin(), System.currentTimeMillis());
            event.getBlock().setMetadata("advanced-dispenser-cooldown", data);
            event.setCancelled(true);
            break;

        }

    }
}
