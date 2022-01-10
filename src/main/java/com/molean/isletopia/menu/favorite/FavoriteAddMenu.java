package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class FavoriteAddMenu extends ListMenu<String> {

    public static List<String> getAvailablePlayer(Player player) {
        List<String> onlinePlayers = ServerInfoUpdater.getOnlinePlayers();
        List<String> collect = CollectionDao.getPlayerCollections(player.getUniqueId())
                .stream()
                .map(UUIDUtils::get)
                .filter(Objects::nonNull)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
        onlinePlayers.removeIf(collect::contains);

        return onlinePlayers;
    }

    public FavoriteAddMenu(Player player) {
        super(player, Component.text("选择你想要收藏的岛屿/玩家"));
        this.convertFunction(HeadUtils::getSkullWithIslandInfo);
        this.components(getAvailablePlayer(player));
        this.onClickSync(s -> {
            new SyncThenAsyncTask<>(() -> {
                player.performCommand("is star " + s);
                return null;
            }, o -> this.components(getAvailablePlayer(player))).run();
        });
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回收藏菜单").build());
        this.onCloseAsync(() -> {
            new FavoriteMenu(player).open();
        });
    }

}
