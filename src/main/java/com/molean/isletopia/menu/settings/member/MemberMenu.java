package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.menu.settings.SettingsMenu;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MemberMenu extends ChestMenu {

    public MemberMenu(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter, Player player) {

        super(player, 1, Component.text(MessageUtils.getMessage(player, "menu.member.title")));
        ItemStackSheet add = new ItemStackSheet(Material.TORCH, MessageUtils.getMessage(player, "menu.member.add"));
        ItemStackSheet delete = new ItemStackSheet(Material.LEVER, MessageUtils.getMessage(player, "menu.member.remove"));
        ItemStackSheet father = new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main"));
        this
                .itemWithAsyncClickEvent(0, add.build(), () -> new MemberAddMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(2, delete.build(), () -> new MemberRemoveMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .itemWithAsyncClickEvent(8, father.build(), () -> new SettingsMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open());

    }

}
