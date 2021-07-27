package com.molean.isletopia.distribute.individual;

import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClubServer implements CommandExecutor {
    public ClubServer() {
        Objects.requireNonNull(Bukkit.getPluginCommand("clubrealm")).setExecutor(this);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length<1){
            sender.sendMessage("§c/clubrealm 社团名");
            return true;
        }

        if (ServerInfoUpdater.getServers().contains("club_" + args[0])) {
            ServerMessageUtils.switchServer( sender.getName(),"club_" + args[0]);
        }else{
            sender.sendMessage("§c社团不存在, 请注意大小写!");
        }
        return true;
    }
}
