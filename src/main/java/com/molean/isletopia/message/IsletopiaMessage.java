package com.molean.isletopia.message;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.handler.*;
import com.molean.isletopia.message.obj.TellMessageRequest;

import java.util.logging.Logger;

public class IsletopiaMessage {
    public IsletopiaMessage() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            ServerMessageManager.init();
            new VisitRequestHandler();
            new VisitResponseHandler();
            new TeleportRequestHandler();
            new TeleportResponseHandler();
            new PlaySoundRequestHandler();
            new CommandExecuteRequestHandler();
            new TellMessageRequestHandler();

        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia message failed!");
        }
        logger.info("Initialize isletopia message successfully!");
    }
}
