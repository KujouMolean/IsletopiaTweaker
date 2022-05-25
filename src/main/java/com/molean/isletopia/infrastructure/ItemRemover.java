package com.molean.isletopia.infrastructure;

import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageService;
import com.molean.isletopia.shared.pojo.resp.CommonResponseObject;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@Singleton
public class ItemRemover implements Listener {


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
