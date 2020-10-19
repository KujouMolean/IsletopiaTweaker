package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.infrastructure.individual.I18n;
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

public class FavoriteRemoveMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> collections = new ArrayList<>();

    public FavoriteRemoveMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 54, I18n.getMessage("menu.favorite.remove.title",player));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        collections.addAll(UniversalParameter.getParameterAsList(player.getName(), "collection"));
        for (int i = 0; i < collections.size() && i < inventory.getSize() - 1; i++) {
            inventory.setItem(i, HeadUtils.getSkull(collections.get(i)));
        }
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, I18n.getMessage("menu.favorite.remove.return",player));
        inventory.setItem(inventory.getSize() - 1, father.build());

        Bukkit.getScheduler().runTask(IsletopiaTweakers.getPlugin(), () -> player.openInventory(inventory));

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
        if (slot < collections.size()) {
            UniversalParameter.removeParameter(player.getName(), "collection", collections.get(slot));
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new FavoriteRemoveMenu(player).open());
        } else if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new FavoriteMenu(player).open());
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
