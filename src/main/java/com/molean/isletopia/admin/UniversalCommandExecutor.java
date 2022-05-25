package com.molean.isletopia.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.message.ServerInfoUpdater;
import com.molean.isletopia.shared.message.ServerMessageService;
import com.molean.isletopia.shared.pojo.req.CommandExecuteRequest;

@CommandAlias("gcmd")
@Singleton
@CommandPermission("isletopia.gcmd")
public class UniversalCommandExecutor extends BaseCommand {

    @AutoInject
    private ServerMessageService serverMessageService;

    @Default
    public void onDefault(String serverName, String... cmds) {
        String cmd = String.join(" ", cmds);
        CommandExecuteRequest obj = new CommandExecuteRequest(cmd);
        switch (serverName) {
            case "servers" -> {
                for (String server : ServerInfoUpdater.getServers()) {
                    if (server.startsWith("server")) {
                        serverMessageService.sendMessage(server, obj);
                    }
                }
            }
            case "all" -> {
                for (String server : ServerInfoUpdater.getServers()) {
                    serverMessageService.sendMessage(server, obj);
                }
            }
            default -> serverMessageService.sendMessage(serverName, obj);
        }
    }


}
