package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class FavoriteVisitMenu extends ListMenu<String> {

    public static List<String> getAvailablePlayer(Player player) {
        return CollectionDao.getPlayerCollections(player.getUniqueId())
                .stream()
                .map(UUIDUtils::get)
                .collect(Collectors.toList());
    }


    public FavoriteVisitMenu(Player player) {
        super(player, Component.text("选择你想访问的玩家"));
        this.components(getAvailablePlayer(player))
                .convertFunction(HeadUtils::getSkullWithIslandInfo)
                .onClickSync(s -> player.performCommand("visit " + s))
                .closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回收藏菜单").build())
                .onCloseSync(() -> new FavoriteMenu(player).open());
    }
}
