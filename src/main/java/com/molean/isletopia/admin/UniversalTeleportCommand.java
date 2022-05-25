package com.molean.isletopia.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.utils.IsletopiaTweakersUtils;
import org.bukkit.entity.Player;

@CommandAlias("gtp")
@Singleton
@CommandPermission("isletopia.gtp")
public class UniversalTeleportCommand extends BaseCommand {
    @Default
    public void onDefault(Player player,String target) {
        IsletopiaTweakersUtils.universalTeleport(player, target);
    }
}
