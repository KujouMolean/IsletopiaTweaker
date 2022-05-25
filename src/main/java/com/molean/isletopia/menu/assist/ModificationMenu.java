package com.molean.isletopia.menu.assist;

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

public class ModificationMenu extends ChestMenu {


    public ModificationMenu(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter, Player player) {

        super(player, 4, Component.text(MessageUtils.getMessage(player, "menu.modification.title")));
        ItemStackSheet itemStackSheet = ItemStackSheet.fromString(Material.WRITABLE_BOOK, "menu.modification.info");
        this.item(4, itemStackSheet.build());
        this.item(18, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.recipe")).build());
        this.item(19, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.beacon")).build());
        this.item(20, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.sapling")).build());
        this.item(21, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.death")).build());
        this.item(22, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.pig")).build());
        this.item(23, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.mutate")).build());
        this.item(24, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.equipment")).build());
        this.item(25, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.luckycolor")).build());
        this.item(26, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.dimension")).build());
        this.item(27, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.traveler")).build());
        this.item(28, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.piglin")).build());
        this.item(29, ItemStackSheet.fromString(Material.KNOWLEDGE_BOOK, MessageUtils.getMessage(player, "menu.modification.shulker")).build());
        ItemStackSheet father = ItemStackSheet.fromString(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main"));
        itemWithAsyncClickEvent(35, father.build(), () -> new MainMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open());

    }

}
