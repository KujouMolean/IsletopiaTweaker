package com.molean.isletopia.infrastructure;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.infrastructures.ClockMenu;
import com.molean.isletopia.infrastructure.infrastructures.GuideBook;
import com.molean.isletopia.infrastructure.infrastructures.IslandCommand;
import com.molean.isletopia.infrastructure.infrastructures.TeleportSign;

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
        logger.info("Load isletopia infrastructure successfully!");
    }
}
