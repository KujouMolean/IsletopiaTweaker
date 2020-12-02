package com.molean.isletopia.modifier.individual;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class EquipmentWeaker implements Listener {
    public EquipmentWeaker() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    private void handleItemStack(ItemStack result) {
        if (result == null)
            return;

        if (result.getType().name().toLowerCase().contains("wooden")) {
            return;
        }

        ItemMeta itemMeta = result.getItemMeta();
        if (itemMeta == null)
            return;

        int duration = result.getType().getMaxDurability();
        if (duration <= 0)
            return;


        double damage = 50.0 / Math.sqrt(duration) + 2;

        itemMeta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        AttributeModifier attributeModifier = new AttributeModifier(UUID.randomUUID(),
                "EquipmentWeaker-Damage", damage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND);
        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, attributeModifier);
        result.setItemMeta(itemMeta);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        handleItemStack(result);
    }

    @EventHandler
    public void on(PrepareSmithingEvent event) {
        handleItemStack(event.getResult());
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player))
            return;
        Player player = (Player) event.getDamager();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
        handleItemStack(itemInMainHand);
    }

//    @EventHandler
//    public void on(InventoryClickEvent event){
//        handleItemStack(event.getCursor());
//        handleItemStack(event.getCurrentItem());
//    }
//
//    @EventHandler
//    public void on(InventoryMoveItemEvent event){
//        ItemStack item = event.getItem();
//        handleItemStack(item);
//    }

}
