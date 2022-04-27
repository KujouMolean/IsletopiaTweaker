package com.molean.isletopia.infrastructure.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
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
        PluginUtils.registerEvents(this);
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
        if (action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
            if (event.getPlayer().isSneaking()) {
                event.getPlayer().performCommand("is");
            } else {
                event.getPlayer().performCommand("visit " + event.getPlayer().getName());
            }
        }
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            event.getPlayer().performCommand("menu");
        }
        event.setCancelled(true);
    }

    public static ItemStack getClock(Player player) {
        ItemStackSheet itemStackSheet = new ItemStackSheet(Material.CLOCK, MessageUtils.getMessage(player, "player.clock.title"));
        String leftClick = MessageUtils.getMessage(player, "player.clock.leftClick");
        String leftClickFunc = MessageUtils.getMessage(player, "player.clock.leftClick.func");
        String rightClick = MessageUtils.getMessage(player, "player.clock.rightClick");
        String rightClickFunc = MessageUtils.getMessage(player, "player.clock.rightClick.func");
        String shiftLeft = MessageUtils.getMessage(player, "player.clock.shiftLeft");
        String shiftLeftFunc = MessageUtils.getMessage(player, "player.clock.shiftLeft.func");
        itemStackSheet.addLore("%s%s".formatted(leftClick, leftClickFunc));
        itemStackSheet.addLore("%s%s".formatted(rightClick, rightClickFunc));
        itemStackSheet.addLore("%s%s".formatted(shiftLeft, shiftLeftFunc));

        return itemStackSheet.build();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player player = (Player) commandSender;
        player.getInventory().addItem(getClock(player));
        return true;
    }
}