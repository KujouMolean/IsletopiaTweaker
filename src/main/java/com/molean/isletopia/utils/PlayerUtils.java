package com.molean.isletopia.utils;

import com.molean.isletopia.IsletopiaTweakers;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerUtils {
    public static void kickAsync(Player player, String reason) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            player.kick(Component.text(reason));
        });
    }

    public static void giveItem(Player player, ItemStack... itemStacks) {
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            PlayerInventory inventory = player.getInventory();
            for (ItemStack value : inventory.addItem(itemStacks).values()) {
                player.getWorld().dropItem(player.getLocation(), value);
            }
        });
    }
}
