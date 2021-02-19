package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RemoveUnbreakable implements Listener {
    public RemoveUnbreakable() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType().equals(EntityType.PLAYER)) {
            Player player = (Player) event;
            for (ItemStack armorContent : player.getInventory().getArmorContents()) {
                if (armorContent == null) {
                    continue;
                }
                ItemMeta itemMeta = armorContent.getItemMeta();
                itemMeta.setUnbreakable(false);
                armorContent.setItemMeta(itemMeta);
            }
        }
    }

    @EventHandler
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
