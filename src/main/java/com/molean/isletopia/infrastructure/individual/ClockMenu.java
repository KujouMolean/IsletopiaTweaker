package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.ItemStackSheet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClockMenu implements Listener, CommandExecutor {

    public ClockMenu() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        Objects.requireNonNull(Bukkit.getPluginCommand("clock")).setExecutor(this);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Material material = event.getMaterial();
        Action action = event.getAction();
        if (!material.equals(Material.CLOCK))
            return;
        if (event.useItemInHand() == Event.Result.DENY) {
            return;
        }
        if (event.getPlayer().isSneaking()) {
            if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
                event.getPlayer().performCommand("visit " + event.getPlayer().getName());

            }
        }

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            event.getPlayer().performCommand("menu");
        }
        event.setCancelled(true);
    }
    public static ItemStack getClock() {
        ItemStackSheet itemStackSheet = new ItemStackSheet(Material.CLOCK, "§f[§d主菜单§f]§r");
        itemStackSheet.addLore("§f[§f西弗特左键单击§f]§r §f回到§r §f主岛屿§r");
        itemStackSheet.addLore("§f[§7右键单击§f]§r §f打开§r §f主菜单§r");
        return itemStackSheet.build();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        player.getInventory().addItem(getClock());
        return true;
    }
}