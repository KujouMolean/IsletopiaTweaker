package com.molean.isletopia.admin.individual;

import com.molean.isletopia.annotations.BukkitCommand;
import com.molean.isletopia.annotations.Singleton;
import com.molean.isletopia.cloud.CloudInventoryList;
import com.molean.isletopia.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Singleton
@BukkitCommand("isdebug")
public class IsDebugCommand implements CommandExecutor, Listener {

    public IsDebugCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("isdebug")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        CloudInventoryList cloudInventoryList = new CloudInventoryList(player);
        cloudInventoryList.open();

        return true;
    }


}
