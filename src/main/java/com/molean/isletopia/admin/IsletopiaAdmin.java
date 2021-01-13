package com.molean.isletopia.admin;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.admin.individual.UniversalCommandExecutor;
import com.molean.isletopia.admin.individual.UniversalPlayerSender;
import com.molean.isletopia.admin.individual.UniversalTeleportCommand;

import java.util.logging.Logger;

public class IsletopiaAdmin {
    public IsletopiaAdmin() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new UniversalTeleportCommand();
            new UniversalCommandExecutor();
            new UniversalPlayerSender();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia admin failed!");
        }
        logger.info("Initialize isletopia admin successfully!");
    }
}
