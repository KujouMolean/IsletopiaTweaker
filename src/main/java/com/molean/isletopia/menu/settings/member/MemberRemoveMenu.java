package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberRemoveMenu extends ListMenu<String> {

    public static List<String> getAvailablePlayer(Player player) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland != null) {
            return currentIsland.getMembers().stream().map(UUIDUtils::get).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public MemberRemoveMenu(Player player) {
        super(player, Component.text("选择你想取消授权的玩家"));
        this
                .components(getAvailablePlayer(player))
                .convertFunction(HeadUtils::getSkullWithIslandInfo)
                .closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回成员菜单").build())
                .onCloseAsync(() -> new MemberMenu(player).open())
                .onCloseSync(() -> {})
                .onClickSync(s -> new SyncThenAsyncTask<>(() -> {
                    player.performCommand("is distrust " + s);
                    return null;
                }, o -> this.components(getAvailablePlayer(player))).run());
    }
}
