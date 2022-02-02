package com.molean.isletopia.admin.individual;

import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.pojo.req.CommandExecuteRequest;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UniversalCommandExecutor implements CommandExecutor, TabCompleter {
    public UniversalCommandExecutor() {
        Objects.requireNonNull(Bukkit.getPluginCommand("gcmd")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("gcmd")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        if (args.length < 2) {
            return true;
        }
        String serverName = args[0];
        StringBuilder cmd = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            cmd.append(args[i]).append(" ");
        }
        CommandExecuteRequest obj = new CommandExecuteRequest(cmd.toString());
        switch (serverName) {
            case "servers" -> {
                for (String server : ServerInfoUpdater.getServers()) {
                    if (server.startsWith("server")) {
                        ServerMessageUtils.sendMessage(server, "CommandExecuteRequest", obj);
                    }
                }
            }
            case "all" -> {
                for (String server : ServerInfoUpdater.getServers()) {
                    ServerMessageUtils.sendMessage(server, "CommandExecuteRequest", obj);
                }
            }
            default -> ServerMessageUtils.sendMessage(serverName, "CommandExecuteRequest", obj);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            ArrayList<String> strings = new ArrayList<>();
            strings.add("all");
            strings.add("servers");
            strings.addAll(ServerInfoUpdater.getServers());
            strings.removeIf(s -> !s.startsWith(args[0]));
            return strings;
        }else{
            return new ArrayList<>();
        }

    }
}
