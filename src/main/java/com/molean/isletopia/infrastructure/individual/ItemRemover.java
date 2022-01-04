package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemRemover implements Listener {
    public ItemRemover() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void on(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();
        for (int i = 0; i < inventory.getContents().length; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || !item.getType().equals(Material.BOOK)) {
                continue;
            }
            if (item.getEnchantments().keySet().size() == 0) {
                continue;
            }
            inventory.setItem(i, null);
        }
    }
}
