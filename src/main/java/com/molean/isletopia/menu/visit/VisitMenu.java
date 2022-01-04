package com.molean.isletopia.menu.visit;

import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.PlayerMenu;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.utils.HeadUtils;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
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
        super(player, Component.text("选择你想访问的玩家"));

        List<String> onlinePlayers = ServerInfoUpdater.getOnlinePlayers();
        this.components(onlinePlayers);
        this.convertFunction(HeadUtils::getSkullWithIslandInfo)
                .onClickAsync(s -> {
                    UUID uuid = UUIDUtils.get(s);
                    if (uuid == null) {
                        MessageUtils.fail(player, "无法获取对方UUID，访问失败。");
                        close();
                        return;
                    }
                    List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(uuid);
                    new MultiVisitMenu(player, playerIslands)
                            .closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回访问菜单").build())
                            .onCloseAsync(() -> new VisitMenu(player).open()).open();
                })
                .closeItemStack(new ItemStackSheet(Material.BARRIER, "§f返回主菜单").build())
                .onCloseAsync(() -> new PlayerMenu(player).open())
                .onCloseSync(() -> {});
    }

}
