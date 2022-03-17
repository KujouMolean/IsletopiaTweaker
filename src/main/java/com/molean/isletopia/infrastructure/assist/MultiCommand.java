package com.molean.isletopia.infrastructure.assist;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MultiCommand implements CommandExecutor, TabCompleter {

    private final HashMap<String, SubCommand> map = new HashMap<>();

    public MultiCommand(String cmd) {
        Objects.requireNonNull(Bukkit.getPluginCommand(cmd)).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand(cmd)).setExecutor(this);
    }

    public void addSubCommand(SubCommand subCommand) {
        map.put(subCommand.getName(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            return true;
        }
        for (String s : map.keySet()) {
            if (args[0].equalsIgnoreCase(s)) {
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                map.get(s).run(player, subArgs);
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        Player player = (Player) sender;
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 1) {
            strings.addAll(map.keySet());
            strings.removeIf(s -> !s.startsWith(args[0]));
        } else if (args.length > 1) {
            String key = args[0];
            for (String s : map.keySet()) {
                if (key.equalsIgnoreCase(s)) {
                    String[] subArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                    List<String> suggestion = map.get(key).getSuggestion(player, subArgs);
                    strings.addAll(suggestion);
                }
            }
        }
        return strings;
    }
}
