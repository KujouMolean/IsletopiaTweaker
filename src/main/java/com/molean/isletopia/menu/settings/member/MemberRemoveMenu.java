package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.charge.ChargeCommitter;
import com.molean.isletopia.bars.SidebarManager;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.player.PlayerPropertyManager;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MemberRemoveMenu extends ListMenu<String> {

    public static List<String> getAvailablePlayer(Player player) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland != null) {
            return currentIsland.getMembers().stream()
                    .map(UUIDManager::get)
                    .filter(Objects::nonNull)
                    .sorted(String::compareToIgnoreCase)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public MemberRemoveMenu(PlayerPropertyManager playerPropertyManager, SidebarManager sidebarManager, ChargeCommitter chargeCommitter, Player player) {

        super(player, Component.text(MessageUtils.getMessage(player, "menu.member.remove.title")));
        this
                .components(getAvailablePlayer(player))
                .convertFunction(HeadUtils::getSkullWithIslandInfo)
                .closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player, "menu.return.main")).build())
                .onCloseAsync(() -> new MemberMenu(playerPropertyManager, sidebarManager, chargeCommitter, player).open())
                .onCloseSync(null)
                .onClickSync(s -> new SyncThenAsyncTask<>(() -> {
                    player.performCommand("is distrust " + s);
                    return null;
                }, o -> this.components(getAvailablePlayer(player))).run());
    }
}
