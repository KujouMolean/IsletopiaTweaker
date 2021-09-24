package com.molean.isletopia.menu;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.Island;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.charge.PlayerChargeMenu;
import com.molean.isletopia.menu.favorite.FavoriteMenu;
import com.molean.isletopia.menu.inbox.InboxMenu;
import com.molean.isletopia.menu.settings.SettingsMenu;
import com.molean.isletopia.menu.visit.VisitMenu;
import com.molean.isletopia.utils.HeadUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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
        favorite.addLore("§7被收藏的岛主上上线会提示你");
        favorite.addLore("§7被收藏的岛主聊天时名称彩色");
        inventory.setItem(20, favorite.build());
        ItemStackSheet others = new ItemStackSheet(Material.FEATHER, "§f平行世界");
        others.addLore("§7去看看与你同时发展的其他玩家");
        others.addLore("§7这里只会显示在线玩家");
        others.addLore("§7(指令/visit XXX)");
        inventory.setItem(22, others.build());
        ItemStackSheet settings = new ItemStackSheet(Material.LEVER, "§f设置");
        settings.addLore("§7更改你岛屿的选项");
        settings.addLore("§7例如添加岛员,更改生物群系");
        Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        if (!player.getName().equals(currentPlot.getOwner())) {
            settings.setDisplay("§f§m设置");
            settings.addLore("§c你只能修改自己的岛屿");
        }
        inventory.setItem(24, settings.build());
        ItemStackSheet projects = new ItemStackSheet(Material.END_PORTAL_FRAME, "§f历史长廊");
        projects.addLore("§7在这里发现曾在梦幻之屿留下足迹的岛屿");
        projects.addLore("§7在这里为自己的岛屿在梦幻之屿留下足迹");
        projects.addLore("§7该功能正在开发...(暂时无法使用)");
        inventory.setItem(26, projects.build());
        ItemStackSheet bills = new ItemStackSheet(Material.PAPER, "§f生活缴费-此岛-本周");
        others.addLore("§7§m缴纳水费和电费");
        others.addLore("§7§m缴纳水费和电费");
        inventory.setItem(38, bills.build());
        ItemStack skull = HeadUtils.getSkullWithIslandInfo(player.getName());
        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        assert itemMeta != null;
        itemMeta.displayName(Component.text("§f回城"));
        skull.setItemMeta(itemMeta);
        inventory.setItem(40, skull);


        ItemStackSheet mail = new ItemStackSheet(Material.CHEST, "§f邮件");
        mail.addLore("§7活动奖励通过邮件发放");
        mail.addLore("§7该功能目前不稳定");
        inventory.setItem(42, mail.build());

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
                player.sendMessage(Component.text("=>§n点击查看梦幻之屿Wiki§r<=").clickEvent(ClickEvent.openUrl("http://wiki.islet.world")));
                player.closeInventory();
                break;
            case 20:
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new FavoriteMenu(player).open());
                break;
            case 22:
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new VisitMenu(player).open());
                break;
            case 24:
                Island currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
                assert currentPlot != null;
                if (Objects.equals(currentPlot.getOwner(), player.getName())) {
                    Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new SettingsMenu(player).open());
                }
                break;
            case 26:
                //projects
                break;
            case 38:
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new PlayerChargeMenu(player).open());

                break;
            case 40:
                player.performCommand("visit " + player.getName());
                break;
            case 42:
                Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> new InboxMenu(player).open());
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
