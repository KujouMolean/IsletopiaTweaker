package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class SubscribeAddMenu extends ListMenu<String> {

    public static List<String> getAvailablePlayer(Player player) {
        List<String> onlinePlayers = ServerInfoUpdater.getOnlinePlayers();
        List<String> collect = CollectionDao.getPlayerCollections(player.getUniqueId())
                .stream()
                .map(UUIDManager::get)
                .filter(Objects::nonNull)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
        onlinePlayers.removeIf(collect::contains);

        return onlinePlayers;
    }

    public SubscribeAddMenu(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter, Player player) {

        super(player, Component.text(MessageUtils.getMessage(player, "menu.subscribe.add.title")));
        this.convertFunction(HeadUtils::getSkullWithIslandInfo);
        this.components(getAvailablePlayer(player));
        this.onClickSync(s -> {
            new SyncThenAsyncTask<>(() -> {
                player.performCommand("is star " + s);
                return null;
            }, o -> this.components(getAvailablePlayer(player))).run();
        });
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.subscribe")).build());
        this.onCloseAsync(() -> {
            new SubscribeMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open();

        });
    }

}
