package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.modifier.individual.*;

import java.util.logging.Logger;

public class IsletopiaModifier {
    public IsletopiaModifier() {
        Logger logger = IsletopiaTweakers.getPlugin().getLogger();
        try {
            new AddMerchant();
            new RegistRecipe();
            new WoodenItemBooster();
            new FertilizeFlower();
            new HungerKeeper();
            new IronElevator();
            new AdvancedDispenser();
        }catch (Exception exception){
            exception.printStackTrace();
            logger.severe("Initialize isletopia modifier failed!");
        }
        logger.info("Initialize isletopia modifier successfully!");
    }
}
