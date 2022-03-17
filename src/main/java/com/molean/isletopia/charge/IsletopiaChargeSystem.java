package com.molean.isletopia.charge;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.individual.bars.ChargeBar;
import com.molean.isletopia.shared.model.ChargeDetail;

import java.util.logging.Logger;

public class IsletopiaChargeSystem {

    @SuppressWarnings("all")
    public IsletopiaChargeSystem() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        long l = System.currentTimeMillis();
        try{
            new ChargeDetailCommitter();
            new ChargeDetail();
            new ConsumeListener();
            new ChargeBar();
        } catch(Exception exception){
            exception.printStackTrace();
            logger.severe("Initialize isletopia charge system failed!");
        }
        logger.info("Initialize isletopia charge system successfully in " + (System.currentTimeMillis()-l) + "ms");
    }
}
