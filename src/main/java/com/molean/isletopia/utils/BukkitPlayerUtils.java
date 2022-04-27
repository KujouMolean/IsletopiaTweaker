package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.task.Tasks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class BukkitPlayerUtils {
    public static void kickAsync(Player player, String reason) {
        Tasks.INSTANCE.sync( () -> {
            player.kick(Component.text(reason));
        });
    }

    public static void giveItem(Player player, ItemStack... itemStacks) {
        Tasks.INSTANCE.sync( () -> {
            PlayerInventory inventory = player.getInventory();
            for (ItemStack value : inventory.addItem(itemStacks).values()) {
                player.getWorld().dropItem(player.getLocation(), value);
            }
        });
    }

    public static void giveItem(Player player, Material material, int amount) {
        final int[] finalAmount = {amount};
        Tasks.INSTANCE.sync( () -> {
            PlayerInventory inventory = player.getInventory();
            int maxStackSize = material.getMaxStackSize();
            while (finalAmount[0] > 0) {
                int temp;
                if (finalAmount[0] > maxStackSize) {
                    finalAmount[0] -= maxStackSize;
                    temp = maxStackSize;
                }else{
                    temp = finalAmount[0];
                    finalAmount[0] = 0;
                }
                for (ItemStack value : inventory.addItem(new ItemStack(material, temp)).values()) {
                    player.getWorld().dropItem(player.getLocation(), value);
                }
            }
        });
    }
}
