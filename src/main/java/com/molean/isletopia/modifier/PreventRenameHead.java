package com.molean.isletopia.modifier;

import com.molean.isletopia.shared.annotations.Singleton;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Singleton
public class PreventRenameHead implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void on(PrepareAnvilEvent event) {
        ItemStack firstItem = event.getInventory().getFirstItem();
        if (firstItem != null && firstItem.getType().equals(Material.PLAYER_HEAD)) {
            Component originName = firstItem.getItemMeta().displayName();

            ItemStack result = event.getResult();
            if (result == null || !result.getType().equals(Material.PLAYER_HEAD)) {
                return;
            }
            ItemMeta itemMeta = result.getItemMeta();
            itemMeta.displayName(originName);
            result.setItemMeta(itemMeta);
            event.setResult(result);
        }
    }

}
