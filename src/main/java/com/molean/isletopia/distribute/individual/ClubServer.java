package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ClubServer implements CommandExecutor, TabCompleter {
    public ClubServer() {
        Objects.requireNonNull(Bukkit.getPluginCommand("clubrealm")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("clubrealm")).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length<1){
            sender.sendMessage("§c/clubrealm 社团名");
            return true;
        }

        if (ServerInfoUpdater.getServers().contains("club_" + args[0])) {
            MessageUtils.strong(sender, "你即将进入社团子服，输入 /is 回到空岛服.");
            ServerMessageUtils.switchServer( sender.getName(),"club_" + args[0]);
        }else{
            sender.sendMessage("§c社团不存在, 请注意大小写!");
        }
        return true;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        if (args.length == 0) {
            for (String server : ServerInfoUpdater.getServers()) {
                if (server.startsWith("club_")) {
                    String substring = server.substring(5);
                    if (substring.toLowerCase(Locale.ROOT).startsWith(args[0].toLowerCase(Locale.ROOT))) {
                        strings.add(substring);
                    }
                }
            }
        }
        return strings;
    }
}
