package com.molean.isletopia.message;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.admin.individual.UniversalCommandExecutor;
import com.molean.isletopia.message.handler.*;
import com.molean.isletopia.shared.message.RedisMessageListener;
import com.molean.isletopia.shared.message.ServerMessageUtils;
import com.molean.isletopia.shared.pojo.req.CommandExecuteRequest;
import org.bukkit.Server;

import java.util.logging.Logger;

public class IsletopiaMessage {
    public IsletopiaMessage() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        long l = System.currentTimeMillis();
        try {
            new PlaySoundHandler();
            new GiveItemHandler();
            new BeaconRequestHandler();
            new ElytraRequestHandler();
            new VisitRequestHandler();
            new VisitResponseHandler();
            new TeleportRequestHandler();
            new TeleportResponseHandler();
            new PlaySoundRequestHandler();
            new CommandExecuteRequestHandler();
            new TellMessageRequestHandler();
            new PlayTimeRequestHandler();

            //load class
            new ServerMessageUtils();
            new CommandExecuteRequest();

        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia message failed!");
        }
        logger.info("Initialize isletopia message successfully in " + (System.currentTimeMillis()-l) + "ms");
    }
}
