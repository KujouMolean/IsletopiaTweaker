package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.menu.PlayerMenu;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class IsDebugCommand implements CommandExecutor, Listener {

    public IsDebugCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("isdebug")).setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, IsletopiaTweakers.getPlugin());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        new PlayerMenu(player, player.getUniqueId()).open();
        return true;
    }


}
