package com.molean.isletopia.admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.molean.isletopia.shared.annotations.AutoInject;
import com.molean.isletopia.shared.annotations.Singleton;
import com.molean.isletopia.shared.message.ServerMessageService;
import com.molean.isletopia.shared.pojo.req.SwitchServerRequest;

@CommandAlias("gsend")
@Singleton
@CommandPermission("isletopia.gsend")
public class UniversalPlayerSender extends BaseCommand {

    @AutoInject
    private ServerMessageService serverMessageService;
    @Default
    public void onDefault(String target, String server) {
        serverMessageService.sendMessage("proxy", new SwitchServerRequest(target, server));
    }

}
