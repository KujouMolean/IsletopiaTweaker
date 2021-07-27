package com.molean.isletopia.menu;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.individual.MessageUtils;
import com.molean.isletopia.menu.favorite.FavoriteMenu;
import com.molean.isletopia.menu.settings.SettingsMenu;
import com.molean.isletopia.menu.visit.VisitMenu;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.PlotUtils;
import com.plotsquared.core.plot.Plot;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;

public class PlayerMenu implements Listener {
    private final Player player;
    private final Inventory inventory;

    public PlayerMenu(Player player) {
        this.player = player;
        inventory = Bukkit.createInventory(player, 45, Component.text("主菜单"));
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    public void open() {
        for (int i = 0; i < 45; i++) {
            ItemStackSheet itemStackSheet = new ItemStackSheet(Material.GRAY_STAINED_GLASS_PANE, " ");
            inventory.setItem(i, itemStackSheet.build());
        }
        ItemStackSheet bookShelf = new ItemStackSheet(Material.BOOKSHELF, "§f指南大全");
        bookShelf.addLore("§7这里写了服务器的所有设定");
        bookShelf.addLore("§7这里有新人必读的入门教程");
        bookShelf.addLore("§7这里有常见物资的获取方法");
        inventory.setItem(18, bookShelf.build());
        ItemStackSheet favorite = new ItemStackSheet(Material.NETHER_STAR, "§f收藏夹");
        favorite.addLore("§7将你喜欢的岛屿添加到收藏夹");
        favorite.addLore("§7通过收藏夹可以快速访问它们");
        inventory.setItem(20, favorite.build());
        ItemStackSheet others = new ItemStackSheet(Material.FEATHER, "§f平行世界");
        others.addLore("§7去看看与你同时发展的其他玩家");
        others.addLore("§7这里只会显示在线玩家");
        inventory.setItem(22, others.build());
        ItemStackSheet settings = new ItemStackSheet(Material.LEVER, "§f设置");
        settings.addLore("§7更改你岛屿的选项");
        settings.addLore("§7例如添加岛员,更改生物群系");
        Plot currentPlot = PlotUtils.getCurrentPlot(player);
        assert currentPlot != null;
        if (!player.getUniqueId().equals(currentPlot.getOwner())) {
            settings.setDisplay("§f§m设置");
            settings.addLore("§c你只能修改自己的岛屿");
        }
        inventory.setItem(24, settings.build());
        ItemStackSheet projects = new ItemStackSheet(Material.REDSTONE, "§f工程");
        projects.addLore("§7尚未开放");
        inventory.setItem(26, projects.build());
        ItemStack skull = HeadUtils.getSkullWithIslandInfo(player.getName());
        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        assert itemMeta != null;
        itemMeta.displayName(Component.text("§f回城"));
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
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new VisitMenu(player).open());
                break;
            case 24:
                Plot currentPlot = PlotUtils.getCurrentPlot(player);
                assert currentPlot != null;
                if (Objects.equals(currentPlot.getOwner(), player.getUniqueId())) {
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
