package com.molean.isletopia.menu.club;

import com.molean.isletopia.shared.model.Club;
import com.molean.isletopia.shared.utils.PlayerUtils;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ChestMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class ClubInfoMenu extends ChestMenu {
    public ClubInfoMenu(Player player, Club club) {
        super(player, 5, Component.text("社团信息"));
        Material icon = Material.STONE;
        try {
            icon = Material.valueOf(club.getIcon().toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
        }
        ItemStackSheet clubInfo = ItemStackSheet.fromString(icon, club.toString());
        this.item(4, clubInfo.build());

        ArrayList<UUID> uuids = new ArrayList<>(club.getMembers().keySet());
        for (int i = 0; i < uuids.size(); i++) {
            ItemStack skull = HeadUtils.getSkull(UUIDManager.get(uuids.get(i)));
            ItemStackSheet itemStackSheet = ItemStackSheet.fromString(skull, PlayerUtils.getDisplay(uuids.get(i)));

            this.item(18 + i, itemStackSheet.build());
        }
//        ItemStackSheet join = ItemStackSheet.fromString(Material.FIREWORK_ROCKET, """
//                申请加入此社团
//                """);
//        this.item(37, join.build());
//        ItemStackSheet achievement = ItemStackSheet.fromString(Material.FIREWORK_ROCKET, """
//                全部成就
//                """);
//        this.item(39, achievement.build());
//        ItemStackSheet member = ItemStackSheet.fromString(Material.FIREWORK_ROCKET, """
//                社团成员
//                """);
//        this.item(41, member.build());
//        ItemStackSheet clubrealm = ItemStackSheet.fromString(Material.FIREWORK_ROCKET, """
//                社团子服
//                """);
//        this.item(43, clubrealm.build());
    }
}
