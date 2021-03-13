package com.molean.isletopia.message;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.core.ServerMessageManager;
import com.molean.isletopia.message.handler.TeleportRequestHandler;
import com.molean.isletopia.message.handler.TeleportResponseHandler;
import com.molean.isletopia.message.handler.VisitRequestHandler;
import com.molean.isletopia.message.handler.VisitResponseHandler;

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

        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia message failed!");
        }
        logger.info("Initialize isletopia message successfully!");
    }
}
