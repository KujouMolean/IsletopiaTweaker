package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.ParameterDao;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
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
import java.util.UUID;

public class UniversalCommandExecutor implements CommandExecutor, TabCompleter {
    public UniversalCommandExecutor() {
        Objects.requireNonNull(Bukkit.getPluginCommand("gcmd")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("gcmd")).setTabCompleter(this);
        Bukkit.getScheduler().runTaskTimerAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            String serverName = ServerInfoUpdater.getServerName();
            ArrayList<String> keys = ParameterDao.keys(serverName);
            if (keys == null || keys.isEmpty()) {
                return;
            }
            for (String key : keys) {
                String value = UniversalParameter.getParameter(serverName, key);
                if (value.startsWith("cmd")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value.substring(3));
                    UniversalParameter.unsetParameter(serverName, key);
                }
            }
        }, 0, 100);
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
            cmd.append(args[i]);
        }
        switch (serverName) {
            case "servers": {
                for (String server : ServerInfoUpdater.getServers()) {
                    if (server.startsWith("server")) {
                        UniversalParameter.addParameter(server, UUID.randomUUID().toString(), "cmd" + cmd.toString());
                    }
                }
                break;
            }
            case "all": {
                for (String server : ServerInfoUpdater.getServers()) {
                    UniversalParameter.addParameter(server, UUID.randomUUID().toString(), "cmd" + cmd.toString());
                }
                break;
            }
            default:
                UniversalParameter.addParameter(serverName, UUID.randomUUID().toString(), "cmd" + cmd.toString());
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 1) {
            strings.add("all");
            strings.add("servers");
            strings.addAll(ServerInfoUpdater.getServers());
        }
        return strings;
    }
}
