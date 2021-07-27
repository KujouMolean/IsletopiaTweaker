package com.molean.isletopia.menu.visit;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.utils.HeadUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VisitMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> onlinePlayers = new ArrayList<>();
    private final int page;

    public VisitMenu(Player player) {
        this(player, ServerInfoUpdater.getOnlinePlayers(), 0);
    }

    public VisitMenu(Player player, List<String> onlinePlayers, int page) {
        inventory = Bukkit.createInventory(player, 54, Component.text("选择你想访问的岛屿:"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        this.player = player;
        this.onlinePlayers.addAll(onlinePlayers);
        this.onlinePlayers.sort(Comparator.comparing(String::toLowerCase));
        if (page > onlinePlayers.size() / 52) {
            page = 0;
        }
        this.page = page;
    }


    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        for (int i = 0; page * 52 + i < onlinePlayers.size() && i < inventory.getSize() - 2; i++) {
            inventory.setItem(i, HeadUtils.getSkullWithIslandInfo(onlinePlayers.get(page * 52 + i)));
        }
        ItemStackSheet next = new ItemStackSheet(Material.LADDER, "§f下一页");
        inventory.setItem(inventory.getSize() - 2, next.build());
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f返回主菜单");
        inventory.setItem(inventory.getSize() - 1, father.build());

        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory), 1);

    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) {
            return;
        }
        event.setCancelled(true);
        if (!event.getClick().equals(ClickType.LEFT)) {
            return;
        }

        int slot = event.getSlot();
        if (slot < 0) {
            return;
        }
        if (slot == inventory.getSize() - 2) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new VisitMenu(player, onlinePlayers, page + 1).open());
            return;
        }
        if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerMenu(player).open());
            return;
        }
        if (slot < 52 && page * 52 + slot < onlinePlayers.size()) {
            player.performCommand("visit " + onlinePlayers.get(page * 52 + slot));
        }

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
