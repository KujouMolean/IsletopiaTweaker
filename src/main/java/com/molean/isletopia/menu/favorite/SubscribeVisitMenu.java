package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.shared.database.CollectionDao;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SubscribeVisitMenu extends ListMenu<String> {

    public static List<String> getAvailablePlayer(Player player) {
        return CollectionDao.getPlayerCollections(player.getUniqueId())
                .stream()
                .map(UUIDManager::get)
                .collect(Collectors.toList());
    }


    public SubscribeVisitMenu(Player player) {
        super(player, Component.text(MessageUtils.getMessage(player, "menu.subscribe.visit.title")));
        this.components(getAvailablePlayer(player))
                .convertFunction(HeadUtils::getSkullWithIslandInfo)
                .onClickSync(s -> player.performCommand("visit " + s))
                .closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.subscribe")).build())
                .onCloseAsync(() -> new SubscribeMenu(player).open())
                .onCloseSync(null);
    }
}
