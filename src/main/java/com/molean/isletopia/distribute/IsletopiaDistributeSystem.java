package com.molean.isletopia.distribute;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.message.handler.ServerInfoUpdater;
import com.molean.isletopia.distribute.individual.*;

import java.util.logging.Logger;

public class IsletopiaDistributeSystem {
    public IsletopiaDistributeSystem() {


        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {

            new NewbieOperation();
//            new PlayerChatTweaker();
            new TellCommand();
            new VisitCommand();
            new ServerInfoUpdater();
            new LastServerUpdater();
            new ClubServer();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia distribute failed!");
        }
        logger.info("Initialize isletopia distribute successfully!");
    }
}
