package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FavoriteRemoveMenu extends ListMenu<String> {
    public static List<String> getAvailablePlayer(Player player) {
        return CollectionDao.getPlayerCollections(player.getUniqueId())
                .stream()
                .map(UUIDUtils::get)
                .filter(Objects::nonNull)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    public FavoriteRemoveMenu(Player player) {
        super(player, Component.text("选择你不再想要收藏的岛屿/玩家"));
        this
                .convertFunction(HeadUtils::getSkullWithIslandInfo)
                .components(getAvailablePlayer(player))
                .closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回收藏菜单").build())
                .onCloseAsync(() -> new FavoriteMenu(player).open())
                .onClickSync(s -> {
                    new SyncThenAsyncTask<>(() -> {
                        player.performCommand("is unstar " + s);
                        return null;
                    }, o -> this.components(getAvailablePlayer(player))).run();
                });
    }

}
