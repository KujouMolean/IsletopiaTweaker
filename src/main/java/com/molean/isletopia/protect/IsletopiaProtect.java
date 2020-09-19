package com.molean.isletopia.protect;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.modifier.modifiers.AddMerchant;
import com.molean.isletopia.modifier.modifiers.FertilizeFlower;
import com.molean.isletopia.modifier.modifiers.RegistRecipe;
import com.molean.isletopia.modifier.modifiers.WoodenItemBooster;
import com.molean.isletopia.protect.protections.*;

import java.util.logging.Logger;

public class IsletopiaProtect {
    public IsletopiaProtect() {


        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new AnimalProtect();
            new LavaProtect();
            new PlotMobCap();
            new PreventCreeperBreak();
            new RemoveDisgustingMob();
        }catch (Exception exception){
            exception.printStackTrace();
            logger.severe("Initialize isletopia protect failed!");
        }
        logger.info("Load isletopia protect successfully!");

    }
}
