package com.molean.isletopia.virtualmenu.internal;

import com.molean.isletopia.IsletopiaTweakers;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenu implements Menu {
    protected Inventory inventory;
    protected Player player;
    private boolean destroyed = false;

    public InventoryMenu(Player player, InventoryType inventoryType, Component title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, inventoryType, title);
    }

    public InventoryMenu(Player player, InventoryType inventoryType) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, inventoryType);
    }

    public InventoryMenu(Player player, int chestSize, Component title) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, chestSize, title);
    }

    public Inventory inventory() {
        return inventory;
    }

    public Player player() {
        return player;
    }

    public void open() {
        if (destroyed) {
            throw new RuntimeException("Can't open a destroyed menu!");
        }
        VirtualMenuManager.INSTANCE.registerMenu(this);
        beforeOpen();
        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
            this.player.openInventory(inventory);
            afterOpen();
        });

    }


    public InventoryMenu item(int slot, ItemStack itemStack) {
        inventory.setItem(slot, itemStack);
        return this;
    }

    public ItemStack item(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public void close() {
        InventoryView openInventory = player.getOpenInventory();
        if (openInventory.getTopInventory().equals(inventory)) {
            Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> {
                player.closeInventory();
            });
        }
    }

    public void onLeftClick(int slot) {
    }

    public void onShiftLeftClick(int slot) {
    }

    public void onRightClick(int slot) {
    }

    public void onShiftRightClick(int slot) {
    }

    public void onMiddleClick(int slot) {
    }

    public void onNumberKey(int slot) {
    }

    public void onDoubleClick(int slot) {
    }

    public void onDrop(int slot) {
    }

    public void onControlDrop(int slot) {
    }

    public void onSwapOffHand(int slot) {
    }

    public void beforeOpen() {

    }

    public void afterOpen() {

    }

    public void afterClose() {

    }

    public void beforeClick(ClickType clickType, int slot) {

    }

    public void afterClick(ClickType clickType, int slot) {

    }

    public void destroy() {
        inventory = null;
        this.player = null;
        destroyed = true;
    }
}
