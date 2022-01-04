package com.molean.isletopia.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryUtils {
    public static boolean takeItem(Player player, Material material, int amount) {
        int total = 0;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType().equals(material)) {
                total += content.getAmount();
            }
        }
        if (total < amount) {
            return false;
        }
        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType().equals(material)) {
                if (content.getAmount() > amount) {
                    content.setAmount(content.getAmount() - amount);
                    return true;
                } else {
                    amount -= content.getAmount();
                    content.setAmount(0);
                }
            }
        }
        return true;
    }
}
