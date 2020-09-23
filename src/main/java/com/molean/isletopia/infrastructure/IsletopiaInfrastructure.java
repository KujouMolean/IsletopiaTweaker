package com.molean.isletopia.infrastructure;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.individual.ClockMenu;
import com.molean.isletopia.infrastructure.individual.GuideBook;
import com.molean.isletopia.infrastructure.individual.IslandCommand;
import com.molean.isletopia.infrastructure.individual.TeleportSign;

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
        }catch (Exception exception){
            exception.printStackTrace();
            logger.severe("Initialize isletopia infrastructure failed!");
        }
        logger.info("Initialize isletopia infrastructure successfully!");
    }
}
