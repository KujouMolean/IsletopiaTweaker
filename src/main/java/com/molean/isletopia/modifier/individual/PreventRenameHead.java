package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PreventRenameHead implements Listener {
    public PreventRenameHead() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getFirstItem();
        if (firstItem != null && firstItem.getType().equals(Material.PLAYER_HEAD)) {
            String originName = firstItem.getItemMeta().getDisplayName();

            ItemStack result = event.getResult();
            if (result == null || !result.getType().equals(Material.PLAYER_HEAD)) {
                return;
            }
            ItemMeta itemMeta = result.getItemMeta();
            itemMeta.setDisplayName(originName);
            result.setItemMeta(itemMeta);
            event.setResult(result);
        }
    }

}
