package com.molean.isletopia.distribute.individual;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.BungeeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TellCommand implements CommandExecutor, TabCompleter {
    public TellCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("tell")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("tell")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("msg")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("msg")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("w")).setTabCompleter(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("w")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        if (args.length < 2)
            return true;
        Bukkit.getScheduler().runTaskAsynchronously(IsletopiaTweakers.getPlugin(), () -> {
            StringBuilder rawMessage = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                rawMessage.append(args[i]).append(" ");
            }
            String message = "§7" + player.getName() + " -> " + args[0] + ": " + rawMessage;
            if (!ServerInfoUpdater.getOnlinePlayers().contains(args[0])) {
                return;
            }
            BungeeUtils.sendMessageToPlayer(args[0], message);
        });
        return true;
    }




    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            List<String> playerNames = ServerInfoUpdater.getOnlinePlayers();
            playerNames.removeIf(s -> !s.startsWith(args[0]));
            return playerNames;
        } else {
            return new ArrayList<>();
        }
    }


}