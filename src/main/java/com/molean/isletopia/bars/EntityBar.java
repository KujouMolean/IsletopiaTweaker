package com.molean.isletopia.infrastructure.individual.bars;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.protect.individual.IslandMobCap;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.utils.PluginUtils;
import com.molean.isletopia.utils.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@BukkitCommand("entitybar")
public class EntityBar implements CommandExecutor, TabCompleter, Listener {

    private SidebarManager sidebarManager;
    public EntityBar(SidebarManager sidebarManager) {
        this.sidebarManager = sidebarManager;

        Tasks.INSTANCE.intervalAsync(20, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if ("EntityBar".equalsIgnoreCase(sidebarManager.getSidebar(player))) {
                    update(player);
                }
            }
        });
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


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (!"EntityBar".equalsIgnoreCase(sidebarManager.getSidebar(player))) {
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
            sidebarManager.setSidebar(player, "EntityBar");
        } else {
            sidebarManager.setSidebar(player, null);
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return List.of();
    }
}
