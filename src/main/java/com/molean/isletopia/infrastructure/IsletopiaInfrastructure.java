package com.molean.isletopia.infrastructure;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.individual.*;

import java.util.logging.Logger;

public class IsletopiaInfrastructure {
    public IsletopiaInfrastructure() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new ClockMenu();
            new IslandCommand();
            new TeleportSign();
            new GuideBook();
            new ClockMenu();
            new RespawnPoint();
            new MessageUtils();
            new TempServer();
            new StaticMap();
            new MoreChairs();
            new IslandEnterMessage();
            new PlotBackup();
            new OfflineVisitorNotify();
            new ServerBumpReward();
            new MenuCommand();
            new FixSomeProblem();
            new IslandInfoUpdater();
            new FreeRiderWarning();
            new PlayerRidePlayer();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia infrastructure failed!");
        }
        logger.info("Initialize isletopia infrastructure successfully!");
    }
}
