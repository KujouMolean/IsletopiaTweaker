package com.molean.isletopia.infrastructure;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@Singleton
@CommandAlias("clock")
public class ClockMenu extends BaseCommand implements Listener {

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

    @Default
    public void onDefault(Player player) {
        player.getInventory().addItem(getClock(player));
    }
}