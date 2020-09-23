package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.modifier.individual.AddMerchant;
import com.molean.isletopia.modifier.individual.FertilizeFlower;
import com.molean.isletopia.modifier.individual.RegistRecipe;
import com.molean.isletopia.modifier.individual.WoodenItemBooster;

import java.util.logging.Logger;

public class IsletopiaModifier {
    public IsletopiaModifier() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new AddMerchant();
            new RegistRecipe();
            new WoodenItemBooster();
            new FertilizeFlower();
        }catch (Exception exception){
            exception.printStackTrace();
            logger.severe("Initialize isletopia modifier failed!");
        }
        logger.info("Initialize isletopia modifier successfully!");
    }
}
