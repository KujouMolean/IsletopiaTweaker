package com.molean.isletopia.distribute.individual;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.ParameterDao;
import com.molean.isletopia.database.PlotDao;
import com.molean.isletopia.distribute.parameter.UniversalParameter;
import com.molean.isletopia.utils.BungeeUtils;
import com.molean.isletopia.utils.I18n;
import com.molean.isletopia.utils.PlotUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.*;
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
        BungeeUtils.universalPlotVisit(sourcePlayer, target);
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
