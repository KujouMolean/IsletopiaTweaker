package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.shared.utils.UUIDUtils;
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
import java.util.UUID;

public class FavoriteRemoveMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> collections = new ArrayList<>();
    private final int page;

    public FavoriteRemoveMenu(Player player) {
        this(player, new ArrayList<>(CollectionDao.getPlayerCollections(player.getUniqueId())), 0);
    }

    public FavoriteRemoveMenu(Player player, List<UUID> collections, int page) {

        inventory = Bukkit.createInventory(player, 54, Component.text("选择你不再想收藏的岛屿:"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        this.player = player;
        for (UUID collection : collections) {
            this.collections.add(UUIDUtils.get(collection));
        }
        this.collections.sort(Comparator.comparing(String::toLowerCase));
        if (page > collections.size() / 52) {
            page = 0;
        }
        if (collections.size() % 52 == 0 && page == collections.size() / 52) {
            page = 0;
        }
        this.page = page;

    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        for (int i = 0; i + page * 52 < collections.size() && i < inventory.getSize() - 2; i++) {

            inventory.setItem(i, HeadUtils.getSkullWithIslandInfo(collections.get(i + page * 52)));
        }
        ItemStackSheet next = new ItemStackSheet(Material.LADDER, "§f下一页");
        inventory.setItem(inventory.getSize() - 2, next.build());
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f返回主菜单");
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
        if (slot == inventory.getSize() - 2) {
            ArrayList<UUID> uuids = new ArrayList<>();
            for (String collection : collections) {
                uuids.add(UUIDUtils.get(collection));
            }
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new FavoriteRemoveMenu(player, uuids, page + 1).open());
            return;
        }
        if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new FavoriteMenu(player).open());
            return;
        }
        if (slot < collections.size() && slot < 52) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                CollectionDao.removeCollection(player.getUniqueId(), UUIDUtils.get(collections.get(slot + page * 52)));
                new FavoriteRemoveMenu(player).open();
            });
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
