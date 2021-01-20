package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.infrastructure.individual.I18n;
import com.molean.isletopia.menu.ItemStackSheet;
import com.molean.isletopia.utils.HeadUtils;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FavoriteAddMenu implements Listener {

    private final Player player;
    private final Inventory inventory;
    private final List<String> players = new ArrayList<>();
    private final int page;

    private static List<String> getPlayers(Player player) {
        List<String> stringList = new ArrayList<>(ServerInfoUpdater.getOnlinePlayers());
        List<String> collections = UniversalParameter.getParameterAsList(player.getName(), "collection");
        stringList.removeAll(collections);
        stringList.remove(player.getName());
        return stringList;
    }

    public FavoriteAddMenu(Player player) {
        this(player, getPlayers(player), 0);
    }

    public FavoriteAddMenu(Player player, List<String> onlinePlayers, int page) {

        inventory = Bukkit.createInventory(player, 54, I18n.getMessage("menu.favorite.add.title", player));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        this.player = player;
        this.players.addAll(onlinePlayers);
        this.players.sort(Comparator.comparing(String::toLowerCase));
        if (page > onlinePlayers.size() / 52) {
            page = 0;
        }
        if (onlinePlayers.size() % 52 == 0 && page == onlinePlayers.size() / 52) {
            page = 0;
        }
        this.page = page;
    }

    public void open() {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }

        for (int i = 0; i + page * 52 < players.size() && i < inventory.getSize() - 2; i++) {
            inventory.setItem(i, HeadUtils.getSkull(players.get(i + page * 52)));
        }
        ItemStackSheet next = new ItemStackSheet(Material.LADDER, I18n.getMessage("menu.visit.nextPage", player));
        inventory.setItem(inventory.getSize() - 2, next.build());
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, I18n.getMessage("menu.favorite.add.return", player));
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
            ItemStack item = inventory.getItem(slot);
            assert item != null;
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(I18n.getMessage("menu.wait", player));
            item.setItemMeta(itemMeta);
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new FavoriteAddMenu(player, players, page + 1).open());
            return;
        }
        if (slot == inventory.getSize() - 1) {
            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new FavoriteMenu(player).open());
            return;
        }

        if (slot < players.size() && slot < 52) {
            ItemStack item = inventory.getItem(slot);
            assert item != null;
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(I18n.getMessage("menu.wait", player));
            item.setItemMeta(itemMeta);

            Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
                UniversalParameter.addParameter(player.getName(), "collection", players.get(slot + page * 52));
                new FavoriteAddMenu(player).open();
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
