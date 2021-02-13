package com.molean.isletopia.statistics;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.statistics.individual.PlayTime;
import com.molean.isletopia.statistics.individual.vanilla.VanillaStatistic;

import java.util.logging.Logger;

public final class IsletopiaStatistics {
    public IsletopiaStatistics() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new PlayTime();
            new VanillaStatistic();

        } catch (Exception exception) {
            exception.printStackTrace();
            logger.severe("Initialize isletopia statistics failed!");
        }
        logger.info("Initialize isletopia statistics successfully!");
    }
}