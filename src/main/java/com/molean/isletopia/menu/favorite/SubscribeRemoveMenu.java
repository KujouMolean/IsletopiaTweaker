package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SubscribeRemoveMenu extends ListMenu<String> {
    public static List<String> getAvailablePlayer(Player player) {
        return CollectionDao.getPlayerCollections(player.getUniqueId())
                .stream()
                .map(UUIDManager::get)
                .filter(Objects::nonNull)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    public SubscribeRemoveMenu(Player player) {
        super(player, Component.text(MessageUtils.getMessage(player, "menu.subscribe.remove.title")));
        this
                .convertFunction(HeadUtils::getSkullWithIslandInfo)
                .components(getAvailablePlayer(player))
                .closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player,"menu.return.subscribe")).build())
                .onCloseAsync(() -> new SubscribeMenu(player).open())
                .onClickSync(s -> {
                    new SyncThenAsyncTask<>(() -> {
                        player.performCommand("is unstar " + s);
                        return null;
                    }, o -> this.components(getAvailablePlayer(player))).run();
                });
    }

}
