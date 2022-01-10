package com.molean.isletopia.menu;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.menu.assist.AssistMenu;
import com.molean.isletopia.menu.charge.PlayerChargeMenu;
import com.molean.isletopia.menu.favorite.FavoriteMenu;
import com.molean.isletopia.menu.recipe.RecipeListMenu;
import com.molean.isletopia.menu.settings.SettingsMenu;
import com.molean.isletopia.menu.visit.VisitMenu;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class PlayerMenu extends ChestMenu {

    public PlayerMenu(Player player) {
        super(player, 6, Component.text("主菜单"));

        ItemStackSheet bookShelf = ItemStackSheet.fromString(Material.BOOKSHELF, """
                §f指南大全
                §7这里写了服务器的所有设定
                §7这里有新人必读的入门教程
                §7这里有常见物资的获取方法
                §7(右键查看新合成表)
                """);

        ItemStackSheet favorite = ItemStackSheet.fromString(Material.NETHER_STAR, """
                §f收藏夹
                §7将你喜欢的岛屿添加到收藏夹
                §7通过收藏夹可以快速访问它们
                §7被收藏的岛主上上线会提示你
                §7被收藏的岛主聊天时名称彩色
                """);


        ItemStackSheet visits = ItemStackSheet.fromString(Material.FEATHER, """
                §f平行世界
                §7去看看与你同时发展的其他玩家
                §7这里只会显示在线玩家
                §7(指令/visit XXX)
                """);


        ItemStackSheet settings = ItemStackSheet.fromString(Material.LEVER, """
                §f设置
                §7更改你岛屿的选项
                §7例如添加岛员,更改生物群系
                """);


        LocalIsland currentPlot = IslandManager.INSTANCE.getCurrentIsland(player);
        assert currentPlot != null;
        if (!player.getUniqueId().equals(currentPlot.getUuid())) {
            settings.setDisplay("§f§m设置");
            settings.addLore("§c你只能修改自己的岛屿");
        }


        ItemStackSheet bills = ItemStackSheet.fromString(Material.PAPER, """
                §f生活缴费-此岛-本周
                §7缴纳水费和电费
                """);

        ItemStackSheet assist = ItemStackSheet.fromString(Material.BEACON, """
                §f辅助功能
                §7帮助你更舒适地游玩此服务器
                """);

        ItemStackSheet projects = ItemStackSheet.fromString(Material.END_PORTAL_FRAME, """
                §f历史访客
                §7在这里发现最近三天你的岛屿访客
                §7看看你的岛屿有多少人参观
                """);


        ItemStack skullWithIslandInfo = HeadUtils.getSkullWithIslandInfo(player.getName());
        SkullMeta itemMeta = (SkullMeta) skullWithIslandInfo.getItemMeta();
        assert itemMeta != null;
        itemMeta.displayName(Component.text("§f回岛"));
        itemMeta.lore(List.of(Component.text("§f左键回到第一个岛")));
        itemMeta.lore(List.of(Component.text("§f右键打开岛屿列表")));
        skullWithIslandInfo.setItemMeta(itemMeta);
        this
                .item(18, bookShelf.build())
                .clickEventAsync(ClickType.LEFT, 18, () -> {
                    player.sendMessage(Component.text("=>§n点击查看梦幻之屿Wiki§r<=")
                            .clickEvent(ClickEvent.openUrl("http://wiki.islet.world")));
                    player.closeInventory();
                })
                .clickEventAsync(ClickType.RIGHT, 18, () -> new RecipeListMenu(player).open())
                .itemWithAsyncClickEvent(20, favorite.build(), () -> new FavoriteMenu(player).open())
                .itemWithAsyncClickEvent(22, visits.build(), () -> new VisitMenu(player).open())
                .itemWithAsyncClickEvent(24, settings.build(), () -> new SettingsMenu(player).open())
                .itemWithAsyncClickEvent(26, projects.build(), () -> new VisitorMenu(player).open())
                .itemWithAsyncClickEvent(38, bills.build(), () -> new PlayerChargeMenu(player).open())
                .item(40, skullWithIslandInfo)
                .clickEventSync(40, clickType -> {
                    if (clickType.equals(ClickType.LEFT)) {
                        player.performCommand("is");
                        close();
                    } else if (clickType.equals(ClickType.RIGHT)) {
                        player.performCommand("visit " + player.getName());
                        close();
                    }
                })
                .itemWithAsyncClickEvent(42, assist.build(), () -> new AssistMenu(player).open());
    }
}
