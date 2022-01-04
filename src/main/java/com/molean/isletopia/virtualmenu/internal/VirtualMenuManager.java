package com.molean.isletopia.virtualmenu.internal;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum VirtualMenuManager implements Listener {
    INSTANCE;

    private final Map<Inventory, InventoryMenu> registerMap = new HashMap<>();

    VirtualMenuManager() {
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void registerMenu(InventoryMenu menu) {
        registerMap.put(menu.inventory(), menu);
    }

    @EventHandler(ignoreCancelled = true)
    public void onClickSelfInventory(InventoryClickEvent event) {

    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!registerMap.containsKey(inventory)) {
            return;
        }
        event.setCancelled(true);

        if (!Objects.equals(event.getClickedInventory(), event.getInventory())) {
            return;
        }

        InventoryMenu menu = registerMap.get(inventory);
        menu.beforeClick(event.getClick(), event.getSlot());
        switch (event.getClick()) {
            case LEFT -> menu.onLeftClick(event.getSlot());
            case SHIFT_LEFT -> menu.onShiftLeftClick(event.getSlot());
            case RIGHT -> menu.onRightClick(event.getSlot());
            case SHIFT_RIGHT -> menu.onShiftRightClick(event.getSlot());
            case CONTROL_DROP -> menu.onControlDrop(event.getSlot());
            case MIDDLE -> menu.onMiddleClick(event.getSlot());
            case DOUBLE_CLICK -> menu.onDoubleClick(event.getSlot());
            case SWAP_OFFHAND -> menu.onSwapOffHand(event.getSlot());
            case NUMBER_KEY -> menu.onNumberKey(event.getSlot());
            case DROP -> menu.onDrop(event.getSlot());
        }
        menu.afterClick(event.getClick(), event.getSlot());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryDragEvent event) {
        Inventory inventory = event.getInventory();
        if (registerMap.containsKey(inventory)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryMenu menu = registerMap.get(inventory);
        if (menu != null) {
            menu.afterClose();
            menu.destroy();
        }
        registerMap.remove(inventory);

    }
}
