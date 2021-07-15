package com.molean.isletopia.bungee;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.shared.BukkitMessageListener;
import com.molean.isletopia.bungee.individual.*;

import java.util.logging.Logger;

public class IsletopiaBungee {
    public IsletopiaBungee() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new BukkitMessageListener();
            new VisitNotificationHandler();
            new SkinValueHandler();
            new PlaySoundHandler();
            new GiveItemHandler();
            new BeaconRequestHandler();
            new ElytraRequestHandler();

        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia bungee failed!");
        }
        logger.info("Initialize isletopia bungee successfully!");
    }
}
