package com.molean.isletopia.menu.favorite;

import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.menu.MainMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SubscribeMenu extends ChestMenu {
    public SubscribeMenu(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter, Player player) {

        super(player, 1, Component.text(MessageUtils.getMessage(player, "menu.subscribe.tile")));
        ItemStackSheet most = new ItemStackSheet(Material.SOUL_TORCH, MessageUtils.getMessage(player, "menu.subscribe.top"));
        ItemStackSheet visit = new ItemStackSheet(Material.REDSTONE_TORCH, MessageUtils.getMessage(player, "menu.subscribe.visit"));
        ItemStackSheet add = new ItemStackSheet(Material.TORCH, MessageUtils.getMessage(player, "menu.subscribe.add"));
        ItemStackSheet delete = new ItemStackSheet(Material.LEVER, MessageUtils.getMessage(player, "menu.subscribe.remove"));
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main"));
        this
                .itemWithAsyncClickEvent(0, visit.build(), () -> new SubscribeVisitMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(1, add.build(), () -> new SubscribeAddMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(2, delete.build(), () -> new SubscribeRemoveMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(8, father.build(), () -> new MainMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open());
    }
}
