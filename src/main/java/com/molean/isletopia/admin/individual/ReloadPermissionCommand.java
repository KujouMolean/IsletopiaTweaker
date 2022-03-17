package com.molean.isletopia.admin.individual;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReloadPermissionCommand implements CommandExecutor {
    public ReloadPermissionCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("reloadpermission")).setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Bukkit.reloadPermissions();
        commandSender.sendMessage("OK");
        return true;
    }
}
