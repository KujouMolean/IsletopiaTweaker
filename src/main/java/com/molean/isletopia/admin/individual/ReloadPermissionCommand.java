package com.molean.isletopia.admin.individual;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.map.CraftMapRenderer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
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
        MapView map = Bukkit.createMap(IsletopiaTweakers.getWorld());
        MessageUtils.success(commandSender, "OK");
        return true;
    }
}
