package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.virtualmenu.ListMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MemberAddMenu extends ListMenu<String> {

    public static List<String> getAvailablePlayer(Player player) {
        List<String> onlinePlayers = ServerInfoUpdater.getOnlinePlayers();
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland != null) {
            List<String> collect = currentIsland.getMembers().stream().map(UUIDUtils::get).collect(Collectors.toList());
            onlinePlayers.removeIf(collect::contains);
        }
        onlinePlayers.sort(String::compareToIgnoreCase);

        return onlinePlayers;
    }

    public MemberAddMenu(Player player) {
        super(player, Component.text("选择你想授权的玩家"));
        this.components(getAvailablePlayer(player));
        this.convertFunction(HeadUtils::getSkullWithIslandInfo);
        this.onClickSync(s -> {
            new SyncThenAsyncTask<>(() -> {
                player.performCommand("is trust " + s);
                return null;
            }, o -> this.components(getAvailablePlayer(player))).run();

        });
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回成员菜单").build());
        this.onCloseAsync(() -> new MemberMenu(player).open()).onCloseSync(() -> {
        });
    }
}
