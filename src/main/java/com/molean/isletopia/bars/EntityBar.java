package com.molean.isletopia.bars;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.annotations.Interval;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.protect.IslandMobCap;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;


@CommandAlias("entitybar")
@Singleton
public class EntityBar extends BaseCommand {
    private final SidebarManager sidebarManager;
    public EntityBar(SidebarManager sidebarManager) {
        this.sidebarManager = sidebarManager;

    }
    @Interval(value = 20, async = true)
    public void updateEntityBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if ("EntityBar".equalsIgnoreCase(sidebarManager.getSidebar(player))) {
                update(player);
            }
        }
    }


    public static void update(Player player) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
            return;
        }
        Map<String, Integer> snapshot = IslandMobCap.getSnapshot(player, currentIsland.getIslandId());
        if (snapshot == null) {
            return;
        }
        String message = MessageUtils.getMessage(player, "player.bar.entity");
        Tasks.INSTANCE.sync(() -> {
            ScoreboardUtils.updateOrCreatePlayerUniqueSidebar(player, Component.text(message), snapshot);
        });
    }


    @Default
    public void onDefault(Player player) {

        if (!"EntityBar".equalsIgnoreCase(sidebarManager.getSidebar(player))) {
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
            sidebarManager.setSidebar(player, "EntityBar");
        } else {
            sidebarManager.setSidebar(player, null);
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
        }
    }
}
