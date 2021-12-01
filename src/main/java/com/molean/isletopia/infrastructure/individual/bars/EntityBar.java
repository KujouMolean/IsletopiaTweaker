package com.molean.isletopia.infrastructure.individual.bars;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.island.LocalIsland;
import com.molean.isletopia.protect.individual.IslandMobCap;
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

import java.util.*;

public class EntityBar implements CommandExecutor, TabCompleter, Listener {


    public EntityBar() {

        Objects.requireNonNull(Bukkit.getPluginCommand("entitybar")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("entitybar")).setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
        BukkitTask bukkitTask = Bukkit.getScheduler().runTaskTimer(IsletopiaTweakers.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if ("EntityBar".equalsIgnoreCase(SidebarManager.INSTANCE.getSidebar(player.getUniqueId()))) {
                    update(player);
                }
            }
        }, 20, 20);
        IsletopiaTweakers.addDisableTask("Stop update entity bars", bukkitTask::cancel);
    }

    public static void update(Player player) {
        LocalIsland currentIsland = IslandManager.INSTANCE.getCurrentIsland(player);
        if (currentIsland == null) {
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
            return;
        }
        Map<String, Integer> snapshot = IslandMobCap.getSnapshot(currentIsland.getIslandId());
        if (snapshot == null) {
            return;
        }

        ScoreboardUtils.setPlayerUniqueSidebar(player,Component.text("§6实体统计"), snapshot);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if (!"EntityBar".equalsIgnoreCase(SidebarManager.INSTANCE.getSidebar(uuid))) {
            ScoreboardUtils.clearPlayerUniqueSidebar(player);
            SidebarManager.INSTANCE.setSidebar(uuid, "EntityBar");
        } else {
            SidebarManager.INSTANCE.setSidebar(uuid, null);
            ScoreboardUtils.clearPlayerUniqueSidebar(player);

        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return List.of();
    }
}
