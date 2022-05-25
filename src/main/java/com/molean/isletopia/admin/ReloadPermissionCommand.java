package com.molean.isletopia.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.Singleton;
import org.bukkit.Bukkit;

@CommandAlias("reloadPermission")
@Singleton
@CommandPermission("reloadPermission")
public class ReloadPermissionCommand extends BaseCommand {
    @Default
    public void onDefault() {
        Bukkit.reloadPermissions();
    }
}
