package com.molean.isletopia.message;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.handler.*;
import com.molean.isletopia.shared.message.RedisMessageListener;

import java.util.logging.Logger;

public class IsletopiaMessage {
    public IsletopiaMessage() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {


            new VisitNotificationHandler();
            new SkinValueHandler();
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

        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia message failed!");
        }
        logger.info("Initialize isletopia message successfully!");
    }
}
