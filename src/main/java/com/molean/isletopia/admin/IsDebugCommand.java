package com.molean.isletopia.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.cloud.CloudInventoryList;
import org.bukkit.entity.Player;

@Singleton
@CommandAlias("isdebug")
public class IsDebugCommand extends BaseCommand {
    @Default
    public void onDefault(Player player) {
        CloudInventoryList cloudInventoryList = new CloudInventoryList(player);
        cloudInventoryList.open();
    }
}
