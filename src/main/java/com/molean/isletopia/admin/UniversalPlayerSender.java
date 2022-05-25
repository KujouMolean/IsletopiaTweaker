package com.molean.isletopia.admin.individual;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.req.SwitchServerRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@BukkitCommand("gsend")
public class UniversalPlayerSender implements CommandExecutor, TabCompleter {
    public UniversalPlayerSender() {
        Objects.requireNonNull(Bukkit.getPluginCommand("gsend")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("gsend")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        if (!(sender instanceof Player)) {
            return true;
        }
        if (args.length < 2) {
            return true;
        }
        String target = args[0];
        String server = args[1];
        ServerMessageUtils.sendMessage("proxy", "SwitchServer", new SwitchServerRequest(target, server));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 1) {
            strings.addAll(ServerInfoUpdater.getOnlinePlayers());
            strings.removeIf(s -> !s.startsWith(args[0]));
        }
        if (args.length == 2) {
            strings.addAll(ServerInfoUpdater.getServers());
            strings.removeIf(s -> !s.startsWith(args[1]));
        }
        return strings;
    }
}
