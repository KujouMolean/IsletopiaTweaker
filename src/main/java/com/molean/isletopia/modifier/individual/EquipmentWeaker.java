package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class EquipmentWeaker implements Listener {
    public EquipmentWeaker() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @EventHandler
    public void onDamaged(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!entity.getType().equals(EntityType.PLAYER)) {
            return;
        }
        Player player = (Player) entity;
        double damage = event.getDamage();
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        for (ItemStack armorContent : armorContents) {
            if (armorContent == null) {
                continue;
            }
            short maxDurability = armorContent.getType().getMaxDurability();
            if (maxDurability < 10) {
                continue;
            }
            damage *= Math.log(maxDurability) / Math.log(100);
        }
        event.setDamage(damage);
    }
}
