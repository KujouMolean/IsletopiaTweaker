package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class FirstSapling implements Listener {

    private final HashMap<Material, Material> map = new HashMap<>();

    public FirstSapling() {
        PluginUtils.registerEvents(this);
        map.put(Material.ACACIA_LEAVES, Material.ACACIA_SAPLING);
        map.put(Material.OAK_LEAVES, Material.OAK_SAPLING);
        map.put(Material.DARK_OAK_LEAVES, Material.DARK_OAK_SAPLING);
        map.put(Material.BIRCH_LEAVES, Material.BIRCH_SAPLING);
        map.put(Material.SPRUCE_LEAVES, Material.SPRUCE_SAPLING);
        map.put(Material.JUNGLE_LEAVES, Material.JUNGLE_SAPLING);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!map.containsKey(event.getBlock().getType())) {
            return;
        }
        for (Material key : map.keySet()) {
            if (event.getPlayer().getStatistic(Statistic.MINE_BLOCK, key) > 0) {
                return;
            }
        }
        event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(map.get(event.getBlock().getType())));
    }

}
