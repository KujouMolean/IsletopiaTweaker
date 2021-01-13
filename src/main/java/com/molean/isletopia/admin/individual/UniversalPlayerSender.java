package com.molean.isletopia.admin.individual;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.distribute.individual.ServerInfoUpdater;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UniversalPlayerSender implements CommandExecutor, TabCompleter {
    public UniversalPlayerSender() {
        Bukkit.getPluginCommand("gsend").setTabCompleter(this);
        Bukkit.getPluginCommand("gsend").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            return true;
        }
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            return true;
        }
        @SuppressWarnings("all") ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ConnectOther");
        out.writeUTF(args[0]);
        out.writeUTF(args[1]);
        player.sendPluginMessage(IsletopiaTweakers.getPlugin(), "BungeeCord", out.toByteArray());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 1) {
            strings.addAll(ServerInfoUpdater.getOnlinePlayers());
        }
        if (args.length == 2) {
            strings.addAll(ServerInfoUpdater.getServers());
        }
        return strings;
    }
}
