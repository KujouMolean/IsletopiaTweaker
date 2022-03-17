package com.molean.isletopia.menu.visit;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.menu.VisitorMenu;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class VisitMenu extends ListMenu<String> {

    public VisitMenu(Player player) {
        super(player, Component.text(MessageUtils.getMessage(player, "menu.visit.title")));

        List<String> onlinePlayers = ServerInfoUpdater.getOnlinePlayers();
        onlinePlayers.sort(String::compareToIgnoreCase);
        this.components(onlinePlayers);
        this.convertFunction(HeadUtils::getSkullWithIslandInfo)
                .onClickSync(s -> {
                    UUID uuid = UUIDManager.get(s);
                    if (uuid == null) {
                        MessageUtils.fail(player, "menu.visit.failed.uuid");
                        close();
                        return;
                    }
                    new PlayerMenu(player, uuid).itemWithAsyncClickEvent(49, ItemStackSheet.fromString(Material.BARRIER, "§f返回访问菜单").build(), () -> {
                        new VisitorMenu(player).open();
                    }).open();
                })
                .closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main")).build())
                .onCloseAsync(() -> new MainMenu(player).open())
                .onCloseSync(() -> {
                });
    }

}
