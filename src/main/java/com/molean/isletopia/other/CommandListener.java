package com.molean.isletopia.other;

import com.molean.isletopia.IsletopiaTweakers;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiPredicate;

public class CommandListener implements CommandExecutor, TabCompleter {

    public CommandListener() {
        Objects.requireNonNull(Bukkit.getPluginCommand("cmd")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("cmd")).setTabCompleter(this);
    }

    private static final Map<String, BiPredicate<String, Player>> map = new HashMap<>();


    public static void register(String key, BiPredicate<String, Player> biFunction) {
        map.put(key, biFunction);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0) {
            if (map.containsKey(args[0])) {
                if (commandSender instanceof Player player) {
                    if (map.get(args[0]).test(args[0], player)) {
                        map.remove(args[0]);
                    }
                }
            }

        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return new ArrayList<>();
    }
}