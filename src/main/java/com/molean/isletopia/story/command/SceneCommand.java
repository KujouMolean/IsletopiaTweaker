package com.molean.isletopia.story.command;

import com.molean.isletopia.story.SceneManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SceneCommand implements CommandExecutor, TabCompleter {
    public SceneCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("scene")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("scene")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            help((Player) sender);
            return true;
        }
        if (!(sender instanceof Player)) {
            return true;
        }
        String opt = args[0].toLowerCase();
        if ("set".equals(opt)) {
            if (args.length < 3)
                help((Player) sender);
            else
                set((Player) sender, args[1], args[2], ((Player) sender).getLocation());
        } else {
            help((Player) sender);
        }


        return true;
    }

    private void set(Player sender, String scene, String nString, @NotNull Location location) {
        int n = Integer.parseInt(nString);
        boolean set = SceneManager.setScene(sender.getName(), scene, n, location);
        if (!set) {
            sender.sendMessage("§设置失败, 请检查参数是否正确.");
        }
    }

    private void help(Player player) {
        player.sendMessage("故事场景设置:");
        player.sendMessage("/story set <故事名称> <场景序号>");
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
