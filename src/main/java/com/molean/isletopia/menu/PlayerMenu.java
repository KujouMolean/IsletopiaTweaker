package com.molean.isletopia.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerMenu implements Listener {
    private final Player player;
    private final Inventory inventory;

    public PlayerMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 54, "主菜单");
    }

    public void open() {

        DoubleChestInventory chestInventory = (DoubleChestInventory) inventory;
        for (int i = 0; i < 54; i++) {
            ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(" ");
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);
        }

        ItemStack bookShelf = new ItemStack(Material.BOOKSHELF);
        ItemMeta itemMeta = bookShelf.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("[玩家指南]");
        List<String> lores = new ArrayList<>();
        lores.add("");
        itemMeta.setLore(lores);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);

        inventory.set


        ClickType click = event.getClick();
        if (click.)
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.getHandlers().unregister(this);
    }
}
