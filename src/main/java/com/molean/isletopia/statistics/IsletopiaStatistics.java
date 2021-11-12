package com.molean.isletopia.statistics;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.statistics.individual.PlayTime;

import java.util.logging.Logger;

public final class IsletopiaStatistics {
    public IsletopiaStatistics() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        long l = System.currentTimeMillis();
        try {
            new PlayTime();
        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia statistics failed!");
        }
        logger.info("Initialize isletopia statistics successfully in " + (System.currentTimeMillis()-l) + "ms");
    }
}