package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Material.*;

public class AdvancedDispenser implements Listener {
    private final Map<Material, Material> replace = new HashMap<>();

    public AdvancedDispenser() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        replace.put(POTATO, POTATOES);
        replace.put(CARROT, CARROTS);
        replace.put(WHEAT_SEEDS, WHEAT);
        replace.put(PUMPKIN_SEEDS, PUMPKIN_STEM);
        replace.put(MELON_SEEDS, MELON_STEM);
        replace.put(BEETROOT_SEEDS, BEETROOTS);
        replace.put(SWEET_BERRIES, SWEET_BERRY_BUSH);
        replace.put(COCOA, COCOA_BEANS);
    }


    @EventHandler(ignoreCancelled = true)
    public void onDispenser(BlockDispenseEvent event) {
        if (!event.getBlock().getType().equals(DISPENSER)) {
            return;
        }

        Directional directional = (Directional) event.getBlock().getBlockData();
        Block relative = event.getBlock().getRelative(directional.getFacing());
        List<MetadataValue> metadata = event.getBlock().getMetadata("advanced-dispenser-cooldown");
        if (metadata.size() > 0) {
            if (System.currentTimeMillis() - metadata.get(0).asLong() > 200) {
                event.getBlock().removeMetadata("advanced-dispenser-cooldown", IsletopiaTweakers.getPlugin());
            } else {
                event.setCancelled(true);
                return;
            }

        }

        //confirmed
        Material type = replace.getOrDefault(event.getItem().getType(), event.getItem().getType());
        if (!type.isBlock()) {
            return;
        }
        if (!relative.getType().isAir()) {
            return;
        }
        event.setCancelled(true);


        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            if (!relative.getType().isAir()) {
                return;
            }
            Dispenser container = (Dispenser) event.getBlock().getState();
            for (ItemStack stack : container.getInventory().getContents()) {
                if (stack == null || !stack.isSimilar(event.getItem())) {
                    continue;
                }
                stack.setAmount(stack.getAmount() - 1);
                relative.setType(type, true);
                BlockData cloneData = container.getBlockData().clone();
                event.getBlock().setType(Material.DISPENSER);
                event.getBlock().setBlockData(cloneData);
                FixedMetadataValue data = new FixedMetadataValue(IsletopiaTweakers.getPlugin(), System.currentTimeMillis());
                event.getBlock().setMetadata("advanced-dispenser-cooldown", data);
                break;
            }
        });


    }
}
