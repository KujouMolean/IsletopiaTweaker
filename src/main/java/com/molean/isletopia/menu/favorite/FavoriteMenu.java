package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FavoriteMenu extends ChestMenu {
    public FavoriteMenu(Player player) {
        super(player, 1, Component.text("收藏夹"));
        ItemStackSheet visit = new ItemStackSheet(Material.REDSTONE_TORCH, "§f>> 访问收藏 >>");
        ItemStackSheet add = new ItemStackSheet(Material.TORCH, "§f+ 添加收藏 +");
        ItemStackSheet delete = new ItemStackSheet(Material.LEVER, "§f- 删除收藏 -");
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, "§f<<返回主菜单<<");

        this
                .itemWithAsyncClickEvent(0, visit.build(), () -> new FavoriteVisitMenu(player).open())
                .itemWithAsyncClickEvent(2, add.build(), () -> new FavoriteAddMenu(player).open())
                .itemWithAsyncClickEvent(4, delete.build(), () -> new FavoriteRemoveMenu(player).open())
                .itemWithAsyncClickEvent(8, father.build(), () -> new PlayerMenu(player).open());
    }
}
