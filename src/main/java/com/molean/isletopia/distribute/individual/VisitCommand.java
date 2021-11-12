package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import com.molean.isletopia.utils.MessageUtils;
import com.molean.isletopia.shared.utils.UUIDUtils;
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
        Player sourcePlayer = (Player) sender;
        if (args.length < 1)
            return true;
        String target = args[0];

        if (!target.startsWith("#")) {
            if (UUIDUtils.get(target) == null) {
                target = "#" + target;
            }
        }

        int order = 0;
        if (args.length >= 2) {
            try {
                order = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                MessageUtils.fail(sender, args[1] + "不是有效数字");
                return true;
            }
        }
        IsletopiaTweakersUtils.universalPlotVisitByMessage(sourcePlayer, target, order);
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
