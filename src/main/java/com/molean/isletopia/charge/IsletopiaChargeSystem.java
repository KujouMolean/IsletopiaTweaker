package com.molean.isletopia.charge;

import com.molean.isletopia.IsletopiaTweakers;

import java.util.logging.Logger;

public class IsletopiaChargeSystem {

    @SuppressWarnings("all")
    public IsletopiaChargeSystem() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try{
            new PlayerChargeDetailCommitter();
            new PlayerChargeDetail();
            new PlayerConsumeListener();
        } catch(Exception exception){
            exception.printStackTrace();
            logger.severe("Initialize isletopia charge system failed!");
        }
        logger.info("Initialize isletopia charge system successfully!");
    }
}
