package com.molean.isletopia.bungee;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.bungee.individual.PlayerMessageHandler;
import com.molean.isletopia.bungee.individual.UniversalVisitHandler;
import com.molean.isletopia.bungee.individual.VisitNotificationHandler;

import java.util.logging.Logger;

public class IsletopiaBungee {
    public IsletopiaBungee() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new PlayerMessageHandler();
            new UniversalVisitHandler();
            new VisitNotificationHandler();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia bungee failed!");
        }
        logger.info("Initialize isletopia bungee successfully!");
    }
}
