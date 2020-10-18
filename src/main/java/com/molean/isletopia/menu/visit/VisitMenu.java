package com.molean.isletopia.menu.visit;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.menu.favorite.FavoriteMenu;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.I18n;
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
import java.util.List;

public class VisitMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> onlinePlayers = new ArrayList<>();

    public VisitMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 54, I18n.getMessage("menu.visit.title",player));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        onlinePlayers.addAll(ServerInfoUpdater.getOnlinePlayers());
        for (int i = 0; i < onlinePlayers.size() && i < inventory.getSize() - 1; i++) {
            inventory.setItem(i, HeadUtils.getSkull(onlinePlayers.get(i)));
        }
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, I18n.getMessage("menu.visit.return",player));
        inventory.setItem(inventory.getSize() - 1, father.build());

        Bukkit.getScheduler().runTaskLater(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory),1);

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
        if (slot < onlinePlayers.size()) {
            player.performCommand("visit " + onlinePlayers.get(slot));
        } else if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerMenu(player).open());
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
