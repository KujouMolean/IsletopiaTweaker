package com.molean.isletopia.modifier.modifiers;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WoodenItemBooster implements Listener {
    public WoodenItemBooster() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result == null)
            return;
        if (result.getType().name().toLowerCase().contains("wooden")) {
            ItemMeta itemMeta = result.getItemMeta();
            if (itemMeta == null)
                return;
            itemMeta.setUnbreakable(true);
            result.setItemMeta(itemMeta);
            event.getInventory().setResult(result);
        }
    }
}
