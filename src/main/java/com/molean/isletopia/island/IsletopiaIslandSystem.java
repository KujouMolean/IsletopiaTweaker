package com.molean.isletopia.island;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.database.IslandDao;
import com.molean.isletopia.island.listener.PlayerIslandChangeEventListener;

import java.util.logging.Logger;

public class IsletopiaIslandSystem {
    public IsletopiaIslandSystem() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            IslandDao.checkTable();
            new IslandCommand();
            new PlayerIslandChangeEventListener();
            IslandManager islandManager = IslandManager.INSTANCE;
            IslandFlagManager islandFlagManager = IslandFlagManager.INSTANCE;
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe("Initialize isletopia island system failed!");
        }
        logger.info("Initialize isletopia island system successfully!");
    }
}
