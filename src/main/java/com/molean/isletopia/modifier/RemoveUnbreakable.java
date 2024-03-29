package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Singleton
public class RemoveUnbreakable implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player) {
            for (ItemStack armorContent : player.getInventory().getArmorContents()) {
                if(armorContent!=null){
                    ItemMeta itemMeta = armorContent.getItemMeta();
                    itemMeta.setUnbreakable(false);
                    armorContent.setItemMeta(itemMeta);
                }

            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setUnbreakable(false);
        item.setItemMeta(itemMeta);
    }

}
