package com.molean.isletopia.protect;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.protect.individual.BeaconIslandOption;
import com.molean.isletopia.protect.individual.*;

import java.util.logging.Logger;

public class IsletopiaProtect {
    public IsletopiaProtect() {


        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new AnimalProtect();
            new LavaProtect();
            new PlotMobCap();
            new ExplosionProtect();
            new MobRemover();
            new ElytraLimiter();
            new OtherProtect();
            new BeaconLimiter();
            new BucketUsageProtect();
            new BeaconIslandOption();
        }catch (Exception exception){
            exception.printStackTrace();
            logger.severe("Initialize isletopia protect failed!");
        }
        logger.info("Initialize isletopia protect successfully!");

    }
}
