package com.molean.isletopia.menu.settings.member;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.utils.UUIDManager;
import com.molean.isletopia.task.SyncThenAsyncTask;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.ItemStackSheet;
import com.molean.isletopia.utils.MessageUtils;
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
            List<String> collect = currentIsland.getMembers().stream().map(UUIDManager::get).collect(Collectors.toList());
            onlinePlayers.removeIf(collect::contains);
        }
        onlinePlayers.sort(String::compareToIgnoreCase);

        return onlinePlayers;
    }

    public MemberAddMenu(Player player) {
        super(player, Component.text(MessageUtils.getMessage(player, "menu.member.add.title")));
        this.components(getAvailablePlayer(player));
        this.convertFunction(HeadUtils::getSkullWithIslandInfo);
        this.onClickSync(s -> {
            new SyncThenAsyncTask<>(() -> {
                player.performCommand("is trust " + s);
                return null;
            }, o -> this.components(getAvailablePlayer(player))).run();

        });
        this.closeItemStack(new ItemStackSheet(Material.BARRIER, MessageUtils.getMessage(player,"menu.return.main")).build());
        this.onCloseAsync(() -> new MemberMenu(player).open())
                .onCloseSync(null);
    }
}
