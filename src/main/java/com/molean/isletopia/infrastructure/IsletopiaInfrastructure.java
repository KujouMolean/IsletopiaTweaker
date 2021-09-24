package com.molean.isletopia.infrastructure;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.individual.*;
import com.molean.isletopia.island.IslandCommand;

import java.util.logging.Logger;

public class IsletopiaInfrastructure {
    public IsletopiaInfrastructure() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {

            new TeleportSign();
            new RespawnPoint();
            new StaticMap();
            new MoreChairs();
            new IslandEnterMessage();
            new ServerBumpReward();
            new MenuCommand();
            new FixSomeProblem();
            new IslandInfoUpdater();
            new PlayerRidePlayer();
            new IronElevator();
            new RailWay();
            new FirstSapling();
            new SaveDownload();
            new IslandBackup();
            new SlimeChunk();
            new IslandVisitRecord();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia infrastructure failed!");
        }
        logger.info("Initialize isletopia infrastructure successfully!");
    }
}
