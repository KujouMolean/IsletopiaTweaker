package com.molean.isletopia.menu;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.individual.I18n;
import com.molean.isletopia.menu.favorite.FavoriteMenu;
import com.molean.isletopia.menu.settings.SettingsMenu;
import com.molean.isletopia.menu.visit.VisitMenu;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
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
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerMenu implements Listener {
    private final Player player;
    private final Inventory inventory;

    public PlayerMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 45, I18n.getMessage("menu.title", player));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < 45; i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        ItemStackSheet bookShelf = new ItemStackSheet(Material.BOOKSHELF, I18n.getMessage("menu.guide", player));
        bookShelf.addLore(I18n.getMessage("menu.guide.1", player));
        bookShelf.addLore(I18n.getMessage("menu.guide.2", player));
        bookShelf.addLore(I18n.getMessage("menu.guide.3", player));
        inventory.setItem(18, bookShelf.build());
        ItemStackSheet favorite = new ItemStackSheet(Material.NETHER_STAR, I18n.getMessage("menu.favorite", player));
        favorite.addLore(I18n.getMessage("menu.favorite.1", player));
        favorite.addLore(I18n.getMessage("menu.favorite.2", player));
        inventory.setItem(20, favorite.build());
        ItemStackSheet others = new ItemStackSheet(Material.FEATHER, I18n.getMessage("menu.visit", player));
        others.addLore(I18n.getMessage("menu.visit.1", player));
        others.addLore(I18n.getMessage("menu.visit.2", player));
        inventory.setItem(22, others.build());
        ItemStackSheet settings = new ItemStackSheet(Material.LEVER, I18n.getMessage("menu.settings", player));
        settings.addLore(I18n.getMessage("menu.settings.1", player));
        settings.addLore(I18n.getMessage("menu.settings.2", player));
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        if (!player.getUniqueId().equals(currentPlot.getOwner())) {
            settings.setDisplay(I18n.getMessage("menu.settings.non-owner", player));
            settings.addLore(I18n.getMessage("menu.settings.non-owner.1", player));
        }
        inventory.setItem(24, settings.build());
        ItemStackSheet projects = new ItemStackSheet(Material.REDSTONE, I18n.getMessage("menu.project", player));
        projects.addLore(I18n.getMessage("menu.project.1", player));
        inventory.setItem(26, projects.build());


        ItemStack skull = HeadUtils.getSkull(player.getName());

        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(I18n.getMessage("menu.home", player));
        skull.setItemMeta(itemMeta);
        inventory.setItem(40, skull);

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
        switch (slot) {
            case 18:
                player.performCommand("guide 指南目录");
                break;
            case 20:
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new FavoriteMenu(player).open());
                break;
            case 22:
                ItemStack item = inventory.getItem(slot);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(I18n.getMessage("menu.wait", player));
                item.setItemMeta(itemMeta);
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new VisitMenu(player).open());
                break;
            case 24:
                Plot currentPlot = PlotUtils.getCurrentPlot(player);
                if (currentPlot.getOwner().equals(player.getUniqueId())) {
                    Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new SettingsMenu(player).open());
                }
                break;
            case 40:
                player.performCommand("visit " + player.getName());
                break;

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
