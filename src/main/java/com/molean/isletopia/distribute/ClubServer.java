package com.molean.isletopia.distribute;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.database.ParameterDao;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageService;
import com.molean.isletopia.shared.utils.LangUtils;
import com.molean.isletopia.shared.utils.Pair;
import com.molean.isletopia.task.Tasks;
import com.molean.isletopia.utils.InventoryUtils;
import com.molean.isletopia.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

@Singleton
@CommandAlias("clubrealm")
public class ClubServer extends BaseCommand {


    @AutoInject
    private ServerMessageService serverMessageService;

    @Default
    public void onDefault(Player player, String serverName, @Default("true") boolean confirm) {
        if (!serverName.startsWith("club_")) {
            serverName = "club_" + serverName;
        }
        String finalServerName = serverName;
        Tasks.INSTANCE.async(() -> {
            if (ServerInfoUpdater.getServers().contains(finalServerName)) {
                MessageUtils.strong(player, "club.realm.enter");
                serverMessageService.switchServer(player.getName(), finalServerName);
            } else {
                MessageUtils.fail(player, "club.realm.notFound");
            }
        });
    }

}
