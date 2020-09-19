package com.molean.isletopia.modifier;

import com.molean.isletopia.IsletopiaTweakers;
import com.molean.isletopia.infrastructure.infrastructures.ClockMenu;
import com.molean.isletopia.infrastructure.infrastructures.GuideBook;
import com.molean.isletopia.infrastructure.infrastructures.IslandCommand;
import com.molean.isletopia.infrastructure.infrastructures.TeleportSign;
import com.molean.isletopia.modifier.modifiers.AddMerchant;
import com.molean.isletopia.modifier.modifiers.FertilizeFlower;
import com.molean.isletopia.modifier.modifiers.RegistRecipe;
import com.molean.isletopia.modifier.modifiers.WoodenItemBooster;

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
        logger.info("Load isletopia modifier successfully!");
    }
}
