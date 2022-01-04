package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.island.IslandManager;
import com.molean.isletopia.menu.visit.MultiVisitMenu;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.model.Island;
import com.molean.isletopia.shared.utils.UUIDUtils;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class VisitCommand implements CommandExecutor, TabCompleter, Listener {

    public VisitCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("visit")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("visit")).setTabCompleter(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        //visit player
        //visit server3 1 5
        //visit player n
        //visit #123123
        Player sourcePlayer = (Player) sender;
        Tasks.INSTANCE.async(() -> {
            if (args.length < 1) {
                return;
            }

            //try player name
            String target = args[0];
            UUID targetUUID = UUIDUtils.get(target);
            if (targetUUID == null) {
                targetUUID = UUIDUtils.get("#" + target);
            }
            if (targetUUID == null) {
                MessageUtils.fail(sourcePlayer, "该ID未注册.");
                return;
            }

            List<Island> playerIslands = IslandManager.INSTANCE.getPlayerIslands(targetUUID);
            if (playerIslands.size() == 0) {
                MessageUtils.fail(sourcePlayer, "对方没有岛屿.");

                return;
            }
            playerIslands.sort(Comparator.comparingInt(Island::getId));
            new MultiVisitMenu(sourcePlayer, playerIslands).open();
        });

        return true;
    }


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = ServerInfoUpdater.getOnlinePlayers();
            playerNames.removeIf(s -> !s.toLowerCase().startsWith(args[0].toLowerCase()));
            return playerNames;
        } else {
            return new ArrayList<>();
        }
    }
}
