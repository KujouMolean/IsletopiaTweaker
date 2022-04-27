package com.molean.isletopia.menu.visit;

import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.utils.PlayerUtils;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class VisitMenu extends ListMenu<String> {

    public VisitMenu(Player player) {
        super(player, Component.text(MessageUtils.getMessage(player, "menu.visit.title")));

        List<String> onlinePlayers = ServerInfoUpdater.getOnlinePlayers();
        onlinePlayers.sort(String::compareToIgnoreCase);
        this.components(onlinePlayers);
        this.convertFunction(s -> {
                    UUID uuid = UUIDManager.get(s);
                    ItemStack skull = HeadUtils.getSkull(s);
                    ItemStackSheet itemStackSheet = ItemStackSheet.fromString(skull, PlayerUtils.getDisplay(uuid));
                    return itemStackSheet.build();
                }).onClickAsync(s -> {
                    UUID uuid = UUIDManager.get(s);
                    if (uuid == null) {
                        MessageUtils.fail(player, "menu.visit.failed.uuid");
                        close();
                        return;
                    }
                    new PlayerMenu(player, uuid).itemWithAsyncClickEvent(49, ItemStackSheet.fromString(Material.BARRIER, "§f返回访问菜单").build(), () -> {
                        new VisitMenu(player).open();
                    }).open();
                })
                .closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main")).build())
                .onCloseAsync(() -> new MainMenu(player).open())
                .onCloseSync(null);
    }

}
