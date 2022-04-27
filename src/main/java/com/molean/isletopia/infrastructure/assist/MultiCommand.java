package com.molean.isletopia.infrastructure.assist;

import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MultiCommand implements CommandExecutor, TabCompleter {

    private final HashMap<String, SubCommand> keyCommandMapping = new HashMap<>();
    private final HashMap<String, String> nameMapping = new HashMap<>();

    public MultiCommand(String cmd) {
        Objects.requireNonNull(Bukkit.getPluginCommand(cmd)).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand(cmd)).setTabCompleter(this);
    }


    public MultiCommand addSubCommand(SubCommand subCommand) {
        String key = subCommand.getName().toLowerCase(Locale.ROOT);
        String name = subCommand.getName();
        nameMapping.put(key, name);
        keyCommandMapping.put(key, subCommand);
        return this;
    }

    public <T extends SubCommand> T createSubCommand(T subCommand) {
        String key = subCommand.getName().toLowerCase(Locale.ROOT);
        String name = subCommand.getName();
        nameMapping.put(key, name);
        keyCommandMapping.put(key, subCommand);
        return subCommand;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            return true;
        }
        for (String s : keyCommandMapping.keySet()) {
            String name = args[0];
            String key = name.toLowerCase(Locale.ROOT);
            if (key.equals(s)) {
                String[] subArgs = new String[args.length - 1];
                System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                Tasks.INSTANCE.async(() -> keyCommandMapping.get(s).run(player, subArgs));
            }
        }

        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        Player player = (Player) sender;
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 1) {
            strings.addAll(nameMapping.values());
        } else if (args.length > 1) {
            String key = args[0].toLowerCase(Locale.ROOT);
            for (String s : keyCommandMapping.keySet()) {
                if (key.equalsIgnoreCase(s)) {
                    String[] subArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, subArgs, 0, subArgs.length);
                    List<String> suggestion = new ArrayList<>(keyCommandMapping.get(key).getSuggestion(player, subArgs));
                    strings.addAll(suggestion);
                }
            }
        }
        strings.removeIf(s -> !s.toLowerCase(Locale.ROOT).startsWith(args[args.length - 1].toLowerCase(Locale.ROOT)));
        return strings;
    }

}
