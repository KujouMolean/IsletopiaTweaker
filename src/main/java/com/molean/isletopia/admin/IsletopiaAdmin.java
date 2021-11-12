package com.molean.isletopia.admin;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.admin.individual.*;

import java.util.logging.Logger;

public class IsletopiaAdmin {
    public IsletopiaAdmin() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        long l = System.currentTimeMillis();
        try {
            new UniversalTeleportCommand();
            new UniversalCommandExecutor();
            new UniversalPlayerSender();
            new PlayerServerFirework();
            new CompletelyTransform();
            new IsDebugCommand();
            new IslandAdmin();
            new ReloadPermissionCommand();
            new ClaimFor();

        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia admin failed!");
        }
        logger.info("Initialize isletopia admin successfully in " + (System.currentTimeMillis()-l) + "ms");
    }
}
